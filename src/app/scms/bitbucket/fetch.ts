import fetch from "cross-fetch";
import { Change, ChangesResponse, DiffContent } from "~app/scms/bitbucket/types";
import { ValidScraperResponse } from "~app/types/scraper";

/**
 * Functions to fetch files from Bitbucket to preview and diff
 */

const toFilePathFromDiff = (diff: Change) => diff.path.components.join("/");

const fromFilePathFromDiff = (diff: Change): string =>
  diff.srcPath === undefined
    ? toFilePathFromDiff(diff)
    : diff.srcPath.components.join("/");

const findDiffFromFilePath = (diffs: readonly Change[], filePath: string) =>
  diffs.find(diff => fromFilePathFromDiff(diff) === filePath);

const extractPathsFromDiff = (
  diff: Change
): {
  readonly fromFilePath: string;
  readonly toFilePath: string;
} => ({
  fromFilePath: fromFilePathFromDiff(diff),
  toFilePath: toFilePathFromDiff(diff)
});

const fetchRawFileFromHash = (
  projectCode: string,
  repoName: string,
  filePath: string,
  hash: string
) => {
  if (typeof filePath !== "string") {
    return Promise.resolve(undefined);
  }
  const fetchUrl = new URL(
    `/projects/${projectCode}/repos/${repoName}/raw/${filePath}?at=${hash}`,
    document.URL
  );

  return fetch(fetchUrl.toString()).then((response: Response) =>
    response.ok ? response.text() : undefined
  );
};

const fetchRawFilesFromHashes = (
  projectCode: string,
  repoName: string,
  fromFilePath: string,
  toFilePath: string,
  fromHash: string,
  toHash: string
) =>
  Promise.all([
    fetchRawFileFromHash(projectCode, repoName, toFilePath, toHash),
    fetchRawFileFromHash(projectCode, repoName, fromFilePath, fromHash)
  ]).then(([fileA, fileB]) => ({
    fileA,
    fileB
  }));

export const getFileContentFromDiff = async ({
  path,
  projectCode,
  repoName,
  sourceRepoId,
  sourceCommit,
  targetRepoId,
  targetCommit
}: ValidScraperResponse): Promise<DiffContent | undefined> => {
  const absoluteUrl = `/rest/api/latest/projects/${projectCode}/repos/${repoName}/compare/changes?from=${sourceCommit}&fromRepo=${sourceRepoId}&to=${targetCommit}&toRepo=${targetRepoId}&start=0&limit=1000`;
  return fetch(new URL(absoluteUrl, document.URL).toString())
    .then((response: Response) => response.json())
    .then(({ values }: ChangesResponse) => {
      const diff = findDiffFromFilePath(values, path);
      if (diff !== undefined) {
        const { fromFilePath, toFilePath } = extractPathsFromDiff(diff);
        return fetchRawFilesFromHashes(
          projectCode,
          repoName,
          fromFilePath,
          toFilePath,
          sourceCommit,
          targetCommit
        );
      }
    });
};
