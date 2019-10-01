export type MessageType =
  | "ToggleDiff"
  | "Reset"
  | "Supported"
  | "BitbucketDataScraped";

export type Message = {
  readonly type: MessageType;
  readonly value?: boolean;
};
