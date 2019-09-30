export type Attributed<A> = {
  readonly attributes: A;
};

export type Repository = {
  readonly id: string;
  readonly slug: string;
};

export type Project = {
  readonly key: string;
};

export type Branch = {
  readonly latestCommit: string;
};

export type FilePath = {
  readonly components: readonly string[];
};

export type Ref = {
  readonly repository: Repository;
  readonly latestCommit: string;
};

export type PullRequest = {
  readonly toRef: {
    readonly attributes: Ref;
  };
  readonly fromRef: {
    readonly attributes: Ref;
  };
};

export type CommonPageState = {
  readonly getFilePath: () => Attributed<FilePath>;
  readonly getRepository: () => Attributed<Repository>;
  readonly getProject: () => Attributed<Project>;
};

export type PullRequestPageState = CommonPageState & {
  readonly getPullRequest: () => Attributed<PullRequest>;
};

export type DiffPageState = CommonPageState & {
  readonly getSourceRepository: () => Repository;
  readonly getTargetRepository: () => Repository;
  readonly getTargetBranch: () => {
    readonly attributes: Branch;
  };
  readonly getSourceBranch: () => {
    readonly attributes: Branch;
  };
};

export type ChangesResponseProperties = {
  readonly changeScope: string;
};

export type ChangePath = {
  readonly components: readonly string[];
  readonly parent: string;
  readonly name: string;
  readonly extension: string;
  readonly toString: string;
};

export type ChangeLinks = {
  readonly self: readonly unknown[];
};

export type ChangeProperties = {
  readonly gitChangeType: string;
};

export type Change = {
  readonly contentId: string;
  readonly fromContentId: string;
  readonly path: ChangePath;
  readonly executable: boolean;
  readonly percentUnchanged: number;
  readonly type: string;
  readonly nodeType: string;
  readonly srcExecutable: boolean;
  readonly links: ChangeLinks;
  readonly properties: ChangeProperties;
  readonly srcPath?: ChangePath;
};

export type ChangesResponse = {
  readonly fromHash: string;
  readonly toHash: string;
  readonly properties: ChangesResponseProperties;
  readonly values: readonly Change[];
  readonly size: number;
  readonly isLastPage: boolean;
  readonly start: number;
  readonly limit: number;
  readonly nextPageStart?: number;
};

export type DiffPaths = {
  readonly fromFilePath: string;
  readonly toFilePath: string;
};
