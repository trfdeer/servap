import htm from "https://esm.sh/htm";
import { h } from "https://esm.sh/preact";

const html = htm.bind(h);

export const sourcesToHtml = (folders, sources, onclick) => html`
  <ul class="list-none list-inside">
    ${folders.map(
      (folder) => html`<li>
        <div
          class="text-semibold text-white p-2 m-2 rounded-lg cursor-default select-none text-lg border-y-2 bg-sky-400 border-white"
        >
          ${folder}
        </div>
        <ul class="list-none list-inside">
          ${sources
            .filter((source) => source.folder == folder)
            .map(
              (source) =>
                html`<li
                  class="text-semibold text-black p-2 m-2 ml-8 cursor-pointer rounded-lg text-m hover:bg-sky-300 transition-all duration-150"
                  onclick=${() => onclick(source.title)}
                >
                  <img
                    class="w-6 rounded-full inline mr-4"
                    src="${source.faviconUrl}"
                  />
                  ${source.title}
                </li>`
            )}
        </ul>
      </li>`
    )}
  </ul>
`;
