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

const fetchRawFileFromHash = (filePath, hash) => {
  if (!filePath) {
    return Promise.resolve(undefined);
  }
  const fetchUrl = new URL(`../../raw/${filePath}?at=${hash}`, document.URL);
  console.log(`fetchUrl: [${fetchUrl}]`);

  return fetch(fetchUrl).then(response => {
    // TODO: Why are all added/removed diffs backwards
    console.dir(response);
    return response.ok ? response.text() : undefined;
  });
};

const fetchRawFilesFromHashes = (fromFilePath, toFilePath, fromHash, toHash) =>
  Promise.all([
    fetchRawFileFromHash(fromFilePath, fromHash),
    fetchRawFileFromHash(toFilePath, toHash)
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
    .then(response => {
      console.log("Response received. Streaming JSON");
      return response.json();
    })
    .then(({ values }) => {
      console.dir(values);
      const diff = findDiffFromFilePath(values, path);
      console.dir(diff);
      if (diff) {
        const { fromFilePath, toFilePath } = extractPathsFromDiff(diff);
        console.dir({ fromFilePath, toFilePath });
        return fetchRawFilesFromHashes(
          fromFilePath,
          toFilePath,
          sourceCommit,
          targetCommit
        );
      }
    });
};
