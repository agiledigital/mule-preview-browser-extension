export const getMulePreviewElement = (): HTMLElement | null =>
  document.querySelector(".mp.extension-container");

export const createContainerElement = (): HTMLElement => {
  const mulePreviewElement = document.createElement("div");
  mulePreviewElement.classList.add("mp");
  mulePreviewElement.classList.add("extension-container");
  return mulePreviewElement;
};
