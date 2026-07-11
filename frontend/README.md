# Frontend — Real Estate Chatbot

React + TypeScript + Tailwind CSS chat UI for the Real Estate Chatbot backend.

## Stack

- **Vite** + **React 18** + **TypeScript**
- **Tailwind CSS** for styling
- **React Router v6** — routing + route guards
- **TanStack Query** — server state (conversations)
- **Zustand** — auth session + chat thread state
- **Axios** — HTTP client with JWT attach + 401 → refresh → `/login`
- **react-hook-form** + **zod** — form validation

## Getting started

```bash
cd frontend
npm install
cp .env.example .env      # set VITE_API_BASE_URL if the backend isn't on :8080
npm run dev               # http://localhost:5173
```

The backend must be running (default `http://localhost:8080`). Its CORS config
allows all origins, so no extra setup is needed for local dev.

## Scripts

| Command | Description |
|---|---|
| `npm run dev` | Start the dev server (HMR) |
| `npm run build` | Type-check (`tsc -b`) and build to `dist/` |
| `npm run preview` | Preview the production build |
| `npm run typecheck` | Type-check only |

## Architecture (Domain-Driven Design)

Two bounded contexts — `auth` and `chat` — plus a `shared` kernel. Each domain
is layered; dependencies point inward (presentation → application → domain).

```
src/
├── shared/              # kernel: http client, API envelope, UI primitives
├── domains/
│   ├── auth/
│   │   ├── domain/         # User, AuthTokens, AuthRepository (port)
│   │   ├── application/    # authStore (zustand), use-case hooks, HTTP wiring
│   │   ├── infrastructure/ # authApi (adapter over httpClient)
│   │   └── presentation/   # Login/Register pages + forms
│   └── chat/
│       ├── domain/         # Conversation, Message, repositories (ports)
│       ├── application/    # chatStore, useSendMessage, useConversations, …
│       ├── infrastructure/ # chatApi, conversationApi (adapters)
│       └── presentation/   # ChatPage, Sidebar, MessageSection, bubbles, composer
└── router/              # AppRouter + Protected/Guest route guards
```

## Key behaviors

- **Two sections:** `Sidebar` (New chat, conversation history, user profile +
  logout pinned to the bottom) and `MessageSection` (thread + composer).
- **Roles by side/color:** user messages right/primary; assistant left/neutral.
- **Send ↔ Stop:** while a reply is pending the send button becomes a Stop
  button that aborts the request (`AbortController`); the typed text is restored
  so you can resume/retry.
- **Thinking indicator:** animated dots render under the last message while a
  reply is pending.
- **JWT expiry:** a 401 triggers a single refresh attempt; on failure the
  session is cleared and the user is routed to `/login`.

## Not yet implemented (seams left in place)

- Rendering the assistant's structured `data[]` (SQL results) as property-listing
  cards — currently only the text reply is shown. See `Message.data` and
  `ChatReply.data`.
