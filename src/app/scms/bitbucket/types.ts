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
