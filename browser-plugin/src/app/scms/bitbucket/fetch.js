import fetch from "cross-fetch";

/**
 * Functions to fetch files from Bitbucket to preview and diff
 */

const toFilePathFromDiff = diff => diff.path.components.join("/");

const fromFilePathFromDiff = diff =>
  diff.srcPath === undefined
    ? toFilePathFromDiff(diff)
    : diff.srcPath.components.join("/");

const findDiffFromFilePath = (diffs, filePath) =>
  diffs.find(diff => fromFilePathFromDiff(diff) === filePath);

const extractPathsFromDiff = diff => ({
  fromFilePath: fromFilePathFromDiff(diff),
  toFilePath: toFilePathFromDiff(diff)
});

const fetchRawFileFromHash = (projectCode, repoName, filePath, hash) => {
  if (!filePath) {
    return Promise.resolve(undefined);
  }
  const fetchUrl = new URL(
    `/projects/${projectCode}/repos/${repoName}/raw/${filePath}?at=${hash}`,
    document.URL
  );

  return fetch(fetchUrl).then(response =>
    response.ok ? response.text() : undefined
  );
};

const fetchRawFilesFromHashes = (
  projectCode,
  repoName,
  fromFilePath,
  toFilePath,
  fromHash,
  toHash
) =>
  Promise.all([
    fetchRawFileFromHash(projectCode, repoName, toFilePath, toHash),
    fetchRawFileFromHash(projectCode, repoName, fromFilePath, fromHash)
  ]).then(([fileA, fileB]) => ({
    fileA,
    fileB
  }));

export const getFileContentFromDiff = ({
  path,
  projectCode,
  repoName,
  sourceRepoId,
  sourceCommit,
  targetRepoId,
  targetCommit
}) => {
  const absoluteUrl = `/rest/api/latest/projects/${projectCode}/repos/${repoName}/compare/changes?from=${sourceCommit}&fromRepo=${sourceRepoId}&to=${targetCommit}&toRepo=${targetRepoId}&start=0&limit=1000`;
  return fetch(new URL(absoluteUrl, document.URL))
    .then(response => response.json())
    .then(({ values }) => {
      const diff = findDiffFromFilePath(values, path);
      if (diff) {
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
