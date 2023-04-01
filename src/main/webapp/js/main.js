import htm from "https://esm.sh/htm";
import { h, render } from "https://esm.sh/preact";
import Markup from "https://esm.sh/preact-markup";
import { useState } from "https://esm.sh/preact/hooks";

import { addFeedModal, editFeedModal } from "./modals.js";
import { sourcesToHtml } from "./sourceTree.js";

const html = htm.bind(h);

const App = () => {
  const [folders, setFolders] = useState([]);

  const [sources, setSources] = useState([]);
  const [selectedSource, setSelectedSource] = useState(null);

  const [links, setLinks] = useState([]);
  const [selectedLink, setSelectedLink] = useState(null);

  const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

  const refreshSources = () => {
    fetch("/sources")
      .then((resp) => resp.json())
      .then((data) => {
        setFolders(data.data.folders);
        setSources(data.data.sources);
      })
      .catch((err) => alert(`Failed to get sources: ${err}`));
  };

  const refreshFeed = () => {
    fetch(`/feed?refresh=1&source=${selectedSource.id}`)
      .then((resp) => resp.json())
      .then((data) => {
        setLinks(data.data);
      })
      .catch((err) => alert(`Failed to get sources: ${err}`));
  };

  const deleteSource = () => {
    fetch(`/sources?feed_id=${selectedSource.id}`, { method: "DELETE" })
      .then((resp) => resp.json())
      .then((data) => {
        alert("Feed Deleted!");
      })
      .catch((err) => alert(`Failed to delete source: ${err}`));
    refreshFeed();
  };

  const processAdd = (ev) => {
    ev.preventDefault();

    let title = encodeURIComponent(
      document.querySelector("#feed_title_input").value
    );
    let url = encodeURIComponent(
      document.querySelector("#feed_url_input").value
    );
    let folder = encodeURIComponent(
      document.querySelector("#feed_folder_input").value
    );

    console.log(title, url, folder);

    fetch("/sources", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: `feed_title=${title}&feed_url=${url}&feed_folder=${folder}`,
    })
      .then((data) => data.json())
      .then((data) => {
        alert(data.message);
        if (!data.error) {
          refreshSources();
        }
      })
      .catch((err) => alert(err));
  };

  const processEdit = (ev) => {
    ev.preventDefault();

    let newTitle = encodeURIComponent(
      document.querySelector("#new_feed_title_input").value
    );
    let newUrl = encodeURIComponent(
      document.querySelector("#new_feed_url_input").value
    );
    let newFolder = encodeURIComponent(
      document.querySelector("#new_feed_folder_input").value
    );

    console.log(selectedSource.id, newTitle, newUrl, newFolder);

    fetch("/sources", {
      method: "PUT",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: `feed_id=${selectedSource.id}&new_feed_title=${newTitle}&new_feed_url=${newUrl}&new_feed_folder=${newFolder}`,
    })
      .then((data) => data.json())
      .then((data) => {
        alert(data.message);
        if (!data.error) {
          refreshSources();
        }
      })
      .catch((err) => alert(err));
  };

  const buttonClasses =
    "w-full flex align-center justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-black bg-sky-400 hover:bg-sky-300 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-white mx-1 transition-all duration-100";

  const sourceClicked = (sourceTitle) => {
    let source = sources.filter((src) => src.title == sourceTitle)[0];
    setSelectedSource(source);

    setLinks([]);
    setSelectedLink(null);

    fetch(`/feed?refresh=0&source=${source.id}`)
      .then((resp) => resp.json())
      .then((data) => {
        setLinks(data.data);
      })
      .catch((err) => alert(`Failed to get sources: ${err}`));
  };

  const linkClicked = (linkId) => {
    console.log(linkId);
    let link = links.filter((it) => it.id == linkId)[0];
    setSelectedLink(link);
  };

  return html`
    <div class="grid grid-cols-12 bg-sky-100">
      <div class="col-span-3 h-screen px-2">
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
      <div
        class="col-span-3 h-screen border-x-2 border-sky-600 overflow-y-auto"
      >
        ${selectedSource == null
          ? html`
              <div class="h-full w-full text-center pt-8">Select a Source</div>
            `
          : html`
              <div class="w-full text-bold text-xl text-center text-black mt-4">
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
                <button class="${buttonClasses}" onclick=${refreshFeed}>
                  Refresh Feed
                </button>
                <button
                  class="${buttonClasses} bg-red-400"
                  onClick=${deleteSource}
                >
                  Delete Feed
                </button>
                <${editFeedModal}
                  id=${selectedSource.id}
                  title=${selectedSource.title}
                  url=${selectedSource.url}
                  folder=${selectedSource.folder}
                  onsubmit=${processEdit}
                />
              </div>
              <div>
                ${links.map(
                  (it) => html`
                    <div
                      class="select-none border-y border-sky-400 py-2 px-1 cursor-pointer hover:bg-sky-200 transition-all duration-100"
                      onclick=${() => linkClicked(it.id)}
                    >
                      <div class="font-bold text-lg">${it.title}</div>
                      <div class="font-italic text-sm">${it.publishDate}</div>
                    </div>
                  `
                )}
              </div>
            `}
      </div>
      <div class="col-span-6 h-screen p-8 overflow-y-auto">
        ${selectedLink == null
          ? html` <div class="h-full w-full text-center">Select a Link</div> `
          : html`
              <div>
                <div class="text-3xl mb-2">${selectedLink.title}</div>
                <div class="w-full text-right italic font-light mb-4">
                  <span>Published on: ${selectedLink.publishDate}</span>
                  <a
                    href="${selectedLink.link}"
                    class="block text-sky-700 underline"
                    >Read more...</a
                  >
                </div>
                <${Markup} type="html" markup="${selectedLink.description}" />
              </div>
            `}
      </div>

      <${addFeedModal} onsubmit=${processAdd} />
    </div>
  `;
};

render(html`<${App} />`, document.querySelector("main#root"));
// ${sources.map((it) => html` <li>${it.name}</li> `)}
