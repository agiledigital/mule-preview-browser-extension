/**
 * Whether the current SCM page is viewing a single file (Preview)
 * or a diff between two files (Diff).
 * Will be None if the the SCM is supported, but the current page doesn't
 * give enough context to use Mule Preview.
 */
export type ScmMode = "None" | "Diff" | "Preview";

/**
 * Represents the content of a file that is being previewed.
 */
export type PreviewContent = string;

/**
 * Represents the content of two files that are being diffed.
 */
export type DiffContent = {
  readonly fileA?: string;
  readonly fileB?: string;
};

/**
 * Defines methods to support a specific SCM (e.g. Bitbucket, Github etc.)
 */
export type ScmModule = {
  /**
   * Returns whether this module is supported on the current page.
   * This should be a short running task that does not do anything too
   * intensive as it will be called every time a page is loaded.
   */
  readonly isSupported: () => Promise<boolean>;

  /**
   * Polled by the host extension to make sure that the page has completely
   * finished loaded. Even if the DOM is ready, the SCM might have some
   * Javascript that needs to finish executing.
   */
  readonly isReady: () => boolean;

  /**
   * Determines what mode the current SCM is in.
   * This call can be longer running than the `isSupported` method because
   * it will only be called if this SCM is detected.
   */
  readonly determineScmMode: () => Promise<ScmMode>;

  /**
   * If the `determineScmMode` returns "Diff", this method will
   * return the content of the two files being diffed.
   */
  readonly getDiffContent: () => Promise<DiffContent>;

  /**
   * If the `determineScmMode` returns "Preview", this method will
   * return the content of the file being previewed.
   */
  readonly getPreviewContent: () => Promise<PreviewContent>;
};
