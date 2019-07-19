/**
 * Functions to fetch files from Bitbucket to preview and diff
 */

const findDiffFromFilePath = (diffs, filePath) =>
  diffs.find(
    diff =>
      diff.destination && diff.source && diff.destination.toString === filePath
  );

const extractPathsFromDiff = diff => ({
  fromFilePath: diff.source.toString,
  toFilePath: diff.destination.toString
});

const fetchRawFileFromHash = (filePath, hash) => {
  const fetchUrl = new URL(`../../raw/${filePath}?at=${hash}`, document.URL);
  console.log(`fetchUrl: [${fetchUrl}]`);
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

export const getFileContentFromDiff = filePath =>
  fetch(document.URL, {
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json"
    }
  })
    .then(response => {
      console.log("Response received. Streaming JSON");
      return response.json();
    })
    .then(({ fromHash, toHash, diffs }) => {
      const diff = findDiffFromFilePath(diffs, filePath);
      if (diff) {
        const { fromFilePath, toFilePath } = extractPathsFromDiff(diff);
        return fetchRawFilesFromHashes(
          fromFilePath,
          toFilePath,
          fromHash,
          toHash
        );
      }
      throw new Error("Cannot diff files with only one state");
    });
