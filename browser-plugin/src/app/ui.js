export const getMulePreviewElement = () =>
  document.querySelector(".mp.extension-container");

export const createContainerElement = () => {
  const mulePreviewElement = document.createElement("div");
  mulePreviewElement.classList.add("mp");
  mulePreviewElement.classList.add("extension-container");
  return mulePreviewElement;
};
