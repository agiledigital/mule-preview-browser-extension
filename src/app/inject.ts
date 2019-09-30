// Thanks: https://gist.github.com/devjin0617/3e8d72d94c1b9e69690717a219644c7a
/**
 * injectScript - Inject internal script to available access to the `window`
 *
 * @param  {type} file_path Local path of the internal script.
 * @param  {type} tag The tag as string, where the script will be append (default: 'body').
 * @see    {@link http://stackoverflow.com/questions/20499994/access-window-variable-from-content-script}
 */
export const injectScript = (
  filePath: string,
  tag: string
): HTMLScriptElement => {
  const node = document.getElementsByTagName(tag)[0];
  const script = document.createElement("script");
  script.setAttribute("type", "text/javascript");
  script.setAttribute("src", filePath);
  return node.appendChild(script);
};
