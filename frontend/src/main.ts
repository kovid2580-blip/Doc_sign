import "./styles.css";

type AuthState = {
  token: string;
  userId: number;
  email: string;
};

type DocumentItem = {
  id: number;
  fileName: string;
  filePath: string;
  uploadedAt: string;
  status: string;
  ownerId: number;
  ownerEmail: string;
};

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

let auth: AuthState | null = loadAuth();
let documents: DocumentItem[] = [];
let selectedDocumentId: number | null = null;
let previewUrl: string | null = null;
let busy = false;
let notice = "";
let activeAuthMode: "login" | "register" = "login";

const app = document.querySelector<HTMLDivElement>("#app");

if (!app) {
  throw new Error("App root not found");
}

render();

function loadAuth(): AuthState | null {
  const raw = localStorage.getItem("doc-signature-auth");
  if (!raw) {
    return null;
  }

  try {
    return JSON.parse(raw) as AuthState;
  } catch {
    localStorage.removeItem("doc-signature-auth");
    return null;
  }
}

function saveAuth(nextAuth: AuthState | null) {
  auth = nextAuth;

  if (nextAuth) {
    localStorage.setItem("doc-signature-auth", JSON.stringify(nextAuth));
  } else {
    localStorage.removeItem("doc-signature-auth");
  }
}

function render() {
  app.innerHTML = `
    <main class="shell">
      <section class="topbar">
        <div>
          <h1>Documents</h1>
          <p>${auth ? escapeHtml(auth.email) : "Sign in to manage uploaded PDFs"}</p>
        </div>
        <div class="topbar-actions">
          ${
            auth
              ? `<button class="ghost" data-action="refresh">Refresh</button>
                 <button class="danger" data-action="logout">Sign out</button>`
              : ""
          }
        </div>
      </section>

      ${
        auth
          ? renderWorkspace()
          : renderAuth()
      }
    </main>
  `;

  bindEvents();
}

function renderAuth() {
  return `
    <section class="auth-panel">
      <div class="segmented" role="tablist">
        <button class="${activeAuthMode === "login" ? "active" : ""}" data-auth-mode="login">Login</button>
        <button class="${activeAuthMode === "register" ? "active" : ""}" data-auth-mode="register">Register</button>
      </div>
      <form class="auth-form" data-form="auth">
        <label>
          Email
          <input name="email" type="email" autocomplete="email" required />
        </label>
        <label>
          Password
          <input name="password" type="password" autocomplete="current-password" required />
        </label>
        <button class="primary" type="submit">${activeAuthMode === "login" ? "Login" : "Create Account"}</button>
      </form>
      ${notice ? `<p class="notice">${escapeHtml(notice)}</p>` : ""}
    </section>
  `;
}

function renderWorkspace() {
  const selectedDocument = documents.find((item) => item.id === selectedDocumentId) ?? null;

  return `
    <section class="workspace">
      <aside class="sidebar">
        <form class="upload-box" data-form="upload">
          <label>
            PDF
            <input name="file" type="file" accept="application/pdf,.pdf" required />
          </label>
          <button class="primary" type="submit">${busy ? "Uploading..." : "Upload"}</button>
        </form>

        <div class="list-head">
          <span>${documents.length} document${documents.length === 1 ? "" : "s"}</span>
        </div>

        <div class="document-list">
          ${
            documents.length
              ? documents.map(renderDocumentRow).join("")
              : `<div class="empty">No uploaded PDFs yet.</div>`
          }
        </div>
      </aside>

      <section class="preview-panel">
        ${
          selectedDocument && previewUrl
            ? `<div class="preview-head">
                 <div>
                   <h2>${escapeHtml(selectedDocument.fileName)}</h2>
                   <p>${formatDate(selectedDocument.uploadedAt)} · ${escapeHtml(selectedDocument.status)}</p>
                 </div>
                 <a class="ghost link-button" href="${previewUrl}" target="_blank" rel="noreferrer">Open</a>
               </div>
               <iframe title="PDF preview" src="${previewUrl}"></iframe>`
            : `<div class="preview-empty">
                 <h2>Preview</h2>
                 <p>Select a document to view it here.</p>
               </div>`
        }
      </section>
    </section>
    ${notice ? `<p class="notice docked">${escapeHtml(notice)}</p>` : ""}
  `;
}

function renderDocumentRow(item: DocumentItem) {
  return `
    <button class="document-row ${selectedDocumentId === item.id ? "selected" : ""}" data-document-id="${item.id}">
      <span class="file-icon">PDF</span>
      <span>
        <strong>${escapeHtml(item.fileName)}</strong>
        <small>${formatDate(item.uploadedAt)} · ${escapeHtml(item.status)}</small>
      </span>
    </button>
  `;
}

function bindEvents() {
  document.querySelectorAll<HTMLButtonElement>("[data-auth-mode]").forEach((button) => {
    button.addEventListener("click", () => {
      activeAuthMode = button.dataset.authMode as "login" | "register";
      notice = "";
      render();
    });
  });

  document.querySelector<HTMLFormElement>('[data-form="auth"]')?.addEventListener("submit", handleAuth);
  document.querySelector<HTMLFormElement>('[data-form="upload"]')?.addEventListener("submit", handleUpload);

  document.querySelector<HTMLButtonElement>('[data-action="refresh"]')?.addEventListener("click", () => {
    loadDocuments();
  });

  document.querySelector<HTMLButtonElement>('[data-action="logout"]')?.addEventListener("click", () => {
    revokePreviewUrl();
    documents = [];
    selectedDocumentId = null;
    saveAuth(null);
    notice = "";
    render();
  });

  document.querySelectorAll<HTMLButtonElement>("[data-document-id]").forEach((button) => {
    button.addEventListener("click", () => {
      const id = Number(button.dataset.documentId);
      previewDocument(id);
    });
  });
}

async function handleAuth(event: SubmitEvent) {
  event.preventDefault();
  const form = event.currentTarget as HTMLFormElement;
  const body = Object.fromEntries(new FormData(form).entries());
  const path = activeAuthMode === "login" ? "/api/users/login" : "/api/users/register";

  try {
    busy = true;
    notice = "";

    const response = await fetch(`${API_BASE}${path}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });

    const data = await readJson(response);

    if (!response.ok) {
      throw new Error(data.message ?? `Request failed with ${response.status}`);
    }

    if (activeAuthMode === "register") {
      activeAuthMode = "login";
      notice = `Account created for ${data.email}. Login to continue.`;
      render();
      return;
    }

    saveAuth({
      token: data.token,
      userId: data.userId,
      email: data.email,
    });

    await loadDocuments(false);
  } catch (error) {
    notice = getErrorMessage(error);
    render();
  } finally {
    busy = false;
  }
}

async function handleUpload(event: SubmitEvent) {
  event.preventDefault();

  if (!auth) {
    return;
  }

  const form = event.currentTarget as HTMLFormElement;
  const formData = new FormData(form);
  formData.append("userId", String(auth.userId));

  try {
    busy = true;
    notice = "";
    render();

    const response = await fetch(`${API_BASE}/api/docs/upload`, {
      method: "POST",
      headers: { Authorization: `Bearer ${auth.token}` },
      body: formData,
    });

    const data = await readJson(response);

    if (!response.ok) {
      throw new Error(data.message ?? `Upload failed with ${response.status}`);
    }

    notice = "Upload complete.";
    await loadDocuments(false);
    await previewDocument(data.id);
  } catch (error) {
    notice = getErrorMessage(error);
    render();
  } finally {
    busy = false;
  }
}

async function loadDocuments(announce = true) {
  if (!auth) {
    return;
  }

  try {
    if (announce) {
      notice = "";
      render();
    }

    const response = await fetch(`${API_BASE}/api/docs?userId=${auth.userId}`, {
      headers: { Authorization: `Bearer ${auth.token}` },
    });

    const data = await readJson(response);

    if (!response.ok) {
      throw new Error(data.message ?? `Could not load documents (${response.status})`);
    }

    documents = data;
    if (!selectedDocumentId && documents.length) {
      selectedDocumentId = documents[0].id;
      await previewDocument(documents[0].id, false);
      return;
    }

    render();
  } catch (error) {
    notice = getErrorMessage(error);
    render();
  }
}

async function previewDocument(documentId: number, rerender = true) {
  if (!auth) {
    return;
  }

  try {
    const response = await fetch(`${API_BASE}/api/docs/${documentId}/preview`, {
      headers: { Authorization: `Bearer ${auth.token}` },
    });

    if (!response.ok) {
      throw new Error(`Could not load preview (${response.status})`);
    }

    revokePreviewUrl();
    const blob = await response.blob();
    previewUrl = URL.createObjectURL(blob);
    selectedDocumentId = documentId;

    if (rerender) {
      render();
    }
  } catch (error) {
    notice = getErrorMessage(error);
    render();
  }
}

function revokePreviewUrl() {
  if (previewUrl) {
    URL.revokeObjectURL(previewUrl);
    previewUrl = null;
  }
}

async function readJson(response: Response) {
  const text = await response.text();
  if (!text) {
    return {};
  }

  try {
    return JSON.parse(text);
  } catch {
    return { message: text };
  }
}

function getErrorMessage(error: unknown) {
  return error instanceof Error ? error.message : "Something went wrong.";
}

function escapeHtml(value: string) {
  return value
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;");
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat("en", {
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(value));
}
