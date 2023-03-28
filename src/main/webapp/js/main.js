import htm from "https://esm.sh/htm";
import { h, render } from "https://esm.sh/preact";
import { useState } from "https://esm.sh/preact/hooks";

import { addFeedModal, editFeedModal } from "./modals.js";
import { sourcesToHtml } from "./sourceTree.js";

const html = htm.bind(h);

const App = () => {
  const [folders, setFolders] = useState([]);
  const [sources, setSources] = useState([]);
  const [selectedSource, setSelectedSource] = useState(null);
  const [selectedLink, setSelectedLink] = useState("");

  const refreshSources = () => {
    fetch("/sources")
      .then((resp) => resp.json())
      .then((data) => {
        setFolders(data.data.folders);
        setSources(data.data.sources);
      })
      .catch((err) => alert(`Failed to get sources: ${err}`));
  };

  const buttonClasses =
    "w-full flex align-center justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-black bg-sky-400 hover:bg-sky-300 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-white mx-1 transition-all duration-100";

  const sourceClicked = (sourceTitle) => {
    let source = sources.filter((src) => src.title == sourceTitle)[0];
    setSelectedSource(source);
  };

  return html`
    <div class="grid grid-cols-12 bg-sky-100">
      <div class="col-span-2 h-screen px-2">
        <div class="flex items-center w-full place-content-evenly my-2">
          <button
            class="${buttonClasses}"
            id="add_feed"
            data-te-toggle="modal"
            data-te-target="#addFeedModal"
          >
            Add Feed
          </button>
          <button
            class="${buttonClasses}"
            id="refresh_sources"
            onclick=${refreshSources}
          >
            Refresh Sources
          </button>
        </div>
        <div class="w-full h-0 border rounded-full border-sky-400" />
        ${sourcesToHtml(folders, sources, sourceClicked)}
      </div>
      <div class="col-span-2 h-screen border-x-2 border-sky-600 px-2">
        ${selectedSource &&
        html`
          <div class="w-full text-bold text-xl text-center text-black">
            ${selectedSource.title}
          </div>
          <div class="flex items-center w-full place-content-evenly my-2">
            <button
              class="${buttonClasses}"
              data-te-toggle="modal"
              data-te-target="#editFeedModal"
            >
              Edit Feed
            </button>
            <button class="${buttonClasses}" onclick=${refreshSources}>
              Refresh Feed
            </button>
            <${editFeedModal}
              title=${selectedSource.title}
              url=${selectedSource.url}
              folder=${selectedSource.folder}
            />
          </div>
        `}
      </div>
      <div class="col-span-8 h-screen px-2">V</div>

      <${addFeedModal} />
    </div>
  `;
};

render(html`<${App} />`, document.querySelector("main#root"));
// ${sources.map((it) => html` <li>${it.name}</li> `)}
