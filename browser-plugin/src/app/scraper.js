// Note: Code in this file has to be injected into the target
// browser so it should be free of dependencies and small as possible
(() => {
  const extractPullRequestData = pageState => {
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

  const extractBranchDiffData = pageState => {
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

  const preparePayload = pageState => {
    if (pageState.getFilePath() === undefined) {
      return { valid: false };
    }

    const path = pageState.getFilePath().attributes.components.join("/");
    const repoName = pageState.getRepository().attributes.slug;
    const projectCode = pageState.getProject().attributes.key;
    const isPullRequest = pageState.getPullRequest() !== undefined;
    const refData = isPullRequest
      ? extractPullRequestData(pageState)
      : extractBranchDiffData(pageState);

    return {
      valid: true,
      ...refData,
      repoName,
      path,
      projectCode
    };
  };

  // Special Bitbucket version of require that it injects into the page
  const pageState = window.require("bitbucket/internal/model/page-state");
  const payload = preparePayload(pageState);

  document.dispatchEvent(
    new CustomEvent("BitbucketDataScraped", {
      detail: payload
    })
  );
})();
