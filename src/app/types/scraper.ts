export type ScraperResponse = ValidScraperResponse | InvalidScraperResponse;

export type DiffDetails = {
  readonly sourceRepoId: string;
  readonly targetRepoId: string;
  readonly targetCommit: string;
  readonly sourceCommit: string;
};

export type ValidScraperResponse = DiffDetails & {
  readonly valid: true;
  readonly repoName: string;
  readonly path: string;
  readonly projectCode: string;
};

export type InvalidScraperResponse = {
  readonly valid: false;
};
