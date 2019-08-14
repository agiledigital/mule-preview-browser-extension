/**
 * Functions to fetch files from Bitbucket to preview and diff
 */

const findDiffFromFilePath = (diffs, filePath) =>
  diffs.find(
    diff =>
      (diff.source && diff.source.toString === filePath) ||
      (diff.destination && diff.destination.toString) === filePath
  );

const extractPathsFromDiff = diff => ({
  fromFilePath: diff.source ? diff.source.toString : undefined,
  toFilePath: diff.destination ? diff.destination.toString : undefined
});

const fetchRawFileFromHash = (filePath, hash) => {
  const fetchUrl = new URL(`../../raw/${filePath}?at=${hash}`, document.URL);
  console.log(`fetchUrl: [${fetchUrl}]`);
  if (!filePath) {
    return Promise.resolve(undefined);
  }
  return fetch(fetchUrl).then(response => response.text());
};

const fetchRawFilesFromHashes = (fromFilePath, toFilePath, fromHash, toHash) =>
  Promise.all([
    fetchRawFileFromHash(fromFilePath, fromHash),
    fetchRawFileFromHash(toFilePath, toHash)
  ]).then(([fileA, fileB]) => ({
    fileA,
    fileB
  }));

//https://stash.agiledigital.com.au/rest/api/latest/projects/FP/repos/sample-mule-project/compare/changes?from=42d1b74fad8af18ee0d88256c4f9dd218dc9c526&fromRepo=761&to=06fbb70f52044f963ca30dcdbd1eb9bc9ccd1a8f&start=0&limit=1000

export const getFileContentFromDiff = ({
  path,
  projectCode,
  repoName,
  sourceRepoId,
  sourceCommit,
  targetRepoId,
  targetCommit
}) =>
  fetch(
    `/rest/api/latest/projects/${projectCode}/repos/${repoName}/compare/changes?from=${sourceCommit}&fromRepo=${sourceRepoId}&to=${targetCommit}&toRepo=${targetRepoId}&start=0&limit=1000`
  )
    .then(response => {
      console.log("Response received. Streaming JSON");
      return response.json();
    })
    .then(({ values }) => {
      const diff = findDiffFromFilePath(values, path);
      if (diff) {
        const { fromFilePath, toFilePath } = extractPathsFromDiff(diff);
        return fetchRawFilesFromHashes(
          fromFilePath,
          toFilePath,
          sourceCommit,
          targetCommit
        );
      }
    });
