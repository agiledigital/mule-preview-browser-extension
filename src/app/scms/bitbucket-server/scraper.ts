import {
  CommonPageState,
  DiffDetails,
  DiffPageState,
  PullRequestPageState,
  ScraperResponse
} from "~app/scms/bitbucket-server/types";
import { MessageType } from "~app/types/messenging";

// Note: Code in this file has to be injected into the target
// browser so it should be free of dependencies and small as possible
(() => {
  const isPullRequest = (
    pageState: unknown
  ): pageState is PullRequestPageState => {
    const prPageState = pageState as PullRequestPageState;
    return (
      prPageState.getPullRequest !== undefined &&
      prPageState.getPullRequest() !== undefined
    );
  };

  const isDiff = (pageState: unknown): pageState is DiffPageState => {
    const diffPageState = pageState as DiffPageState;
    return (
      diffPageState.getSourceRepository !== undefined &&
      diffPageState.getSourceRepository() !== undefined &&
      diffPageState.getTargetRepository !== undefined &&
      diffPageState.getTargetRepository() !== undefined
    );
  };

  const extractPullRequestData = (
    pageState: PullRequestPageState
  ): DiffDetails => {
    const pullRequestData = pageState.getPullRequest().attributes;

    const sourceRepoId = pullRequestData.toRef.attributes.repository.id;
    const targetRepoId = pullRequestData.toRef.attributes.repository.id;

    const targetCommit = pullRequestData.toRef.attributes.latestCommit;
    const sourceCommit = pullRequestData.fromRef.attributes.latestCommit;

    return {
      sourceRepoId,
      targetRepoId,
      targetCommit,
      sourceCommit
    };
  };

  const extractBranchDiffData = (pageState: DiffPageState): DiffDetails => {
    const sourceRepoId = pageState.getSourceRepository().id;
    const targetRepoId = pageState.getTargetRepository().id;

    const targetCommit = pageState.getTargetBranch().attributes.latestCommit;
    const sourceCommit = pageState.getSourceBranch().attributes.latestCommit;
    return {
      sourceRepoId,
      targetRepoId,
      targetCommit,
      sourceCommit
    };
  };

  const preparePayload = (pageState: CommonPageState): ScraperResponse => {
    const filePath = pageState.getFilePath();
    if (filePath === undefined) {
      return { valid: false };
    }

    const path = filePath.attributes.components.join("/");
    const repoName = pageState.getRepository().attributes.slug;
    const projectCode = pageState.getProject().attributes.key;

    const buildResponse = (diffDetails: DiffDetails): ScraperResponse => ({
      valid: true,
      repoName,
      path,
      projectCode,
      ...diffDetails
    });

    if (isPullRequest(pageState)) {
      return buildResponse(extractPullRequestData(pageState));
    } else if (isDiff(pageState)) {
      return buildResponse(extractBranchDiffData(pageState));
    } else {
      return { valid: false };
    }
  };

  // Special Bitbucket version of require that it injects into the page
  const bitbucketPageState = window.require(
    "bitbucket/internal/model/page-state"
  ) as CommonPageState;
  const payload = preparePayload(bitbucketPageState);

  document.dispatchEvent(
    new CustomEvent("BitbucketDataScraped" as MessageType, {
      detail: payload
    })
  );
})();
