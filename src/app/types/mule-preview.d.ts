/**
 * Temporary types until they are exported from the library itself
 */
declare module "@agiledigital/mule-preview" {
  import * as React from "react";

  export interface MulePreviewDiffUrlProps {
    readonly contentUrls: readonly [string?, string?];
    readonly contentRoot: string;
  }

  export interface MulePreviewDiffContentProps {
    readonly contentStrings: readonly [string?, string?];
    readonly contentRoot: string;
  }

  export interface MulePreviewUrlProps {
    readonly contentUrl: string;
    readonly contentRoot: string;
  }

  export interface MulePreviewContentProps {
    readonly contentString: string;
    readonly contentRoot: string;
  }

  const MulePreviewDiffUrl: React.FC<MulePreviewDiffUrlProps>;
  const MulePreviewDiffContent: React.FC<MulePreviewDiffContentProps>;
  const MulePreviewUrl: React.FC<MulePreviewUrlProps>;
  const MulePreviewContent: React.FC<MulePreviewContentProps>;
}
