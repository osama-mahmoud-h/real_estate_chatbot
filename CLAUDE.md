# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Run Commands

```bash
# Build the project
./mvnw clean compile

# Run the application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=BookingChatbotApplicationTests

# Run a single test method
./mvnw test -Dtest=BookingChatbotApplicationTests#contextLoads

# Package the application
./mvnw package
```

## Technology Stack

- **Java 17** with Spring Boot 3.5.9
- **Spring AI 1.1.2** for LLM integration (Google GenAI, Ollama, OpenAI, Anthropic)
- **PostgreSQL with pgvector** for vector storage and embeddings
- **Spring Security** with JWT authentication
- **Spring WebFlux** for reactive streaming
- **Lombok** for reducing boilerplate

## Architecture Overview

This is a real estate chatbot using a **LangGraph-style architecture** with RAG (Retrieval-Augmented Generation), composable chains, and autonomous agents.

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         API LAYER                                    │
│   REST Controllers │ WebSocket │ SSE Streaming                      │
└─────────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    ORCHESTRATION LAYER                               │
│   StateGraph │ CompiledGraph │ Nodes │ Edges │ Checkpointer         │
├─────────────────────────────────────────────────────────────────────┤
│                       CHAIN LAYER                                    │
│   LLMChain │ RAGChain │ RouterChain │ SequentialChain               │
└─────────────────────────────────────────────────────────────────────┘
                                 │
        ┌────────────────────────┼────────────────────────┐
        ▼                        ▼                        ▼
┌──────────────┐    ┌────────────────────┐    ┌──────────────────┐
│ AGENT LAYER  │    │    RAG PIPELINE    │    │   MEMORY LAYER   │
│ ReActAgent   │    │ Loader→Splitter→   │    │ ShortTermMemory  │
│ PlanExecute  │    │ Embedder→VectorDB  │    │ LongTermMemory   │
│ ToolCalling  │    │ →Retriever         │    │ SummaryMemory    │
└──────────────┘    └────────────────────┘    └──────────────────┘
        │                        │                        │
        └────────────────────────┼────────────────────────┘
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      LLM PROVIDER LAYER                              │
│   LLMFactory → GoogleGenAI │ Ollama │ OpenAI │ Anthropic            │
└─────────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER                              │
│   PostgreSQL │ PGVector │ Redis (Cache) │ Message Queue             │
└─────────────────────────────────────────────────────────────────────┘
```

### Package Structure

```
semsem/chatbot/
├── orchestration/              # LangGraph-style workflow engine
│   ├── graph/                  # StateGraph, CompiledGraph, GraphState, GraphEdge
│   ├── node/                   # LLMNode, RAGNode, ToolNode, RouterNode
│   ├── checkpointer/           # State persistence (InMemory, Postgres)
│   └── executor/               # Graph execution engine
│
├── chain/                      # Composable LLM chains
│   ├── Chain.java              # Base interface
│   ├── LLMChain.java           # Simple LLM invocation
│   ├── RAGChain.java           # Retrieval + generation
│   ├── RouterChain.java        # Intent-based routing
│   ├── SequentialChain.java    # Chain of chains
│   └── ConversationChain.java  # With memory support
│
├── service/agent/              # Autonomous AI agents
│   ├── ReActAgent.java         # Reasoning + Acting loop
│   ├── PlanAndExecuteAgent.java # Planning then execution
│   ├── ToolCallingAgent.java   # Function calling
│   └── MultiAgentCoordinator.java # Multi-agent orchestration
│
├── rag/                        # RAG pipeline components
│   ├── loader/                 # PDFLoader, DocxLoader, WebLoader
│   ├── splitter/               # RecursiveCharacter, Semantic, Token
│   ├── retriever/              # Vector, BM25, Hybrid, MultiQuery
│   └── pipeline/               # RAGPipeline orchestration
│
├── vectorstore/                # Vector database abstraction
│   ├── VectorStore.java        # Interface
│   ├── PGVectorStore.java      # PostgreSQL pgvector
│   └── SpringAIVectorStore.java # Spring AI integration
│
├── service/embedding/          # Text embedding services
│   ├── EmbeddingService.java   # Interface
│   ├── GoogleEmbeddingService.java
│   └── OllamaEmbeddingService.java
│
├── service/memory/             # Conversation memory
│   ├── ShortTermMemory.java    # Buffer memory
│   ├── LongTermMemory.java     # Vector-based
│   └── SummaryMemory.java      # Summarized history
│
├── service/llm/                # LLM provider abstraction
│   ├── LLMService.java         # Interface
│   ├── LLMFactory.java         # Provider factory
│   ├── GoogleGenAIService.java
│   ├── OllamaService.java
│   ├── OpenAIService.java
│   └── AnthropicService.java
│
├── tool/                       # Agent tools
│   ├── Tool.java               # Interface
│   ├── ToolRegistry.java       # Tool management
│   ├── realestate/             # Domain-specific tools
│   │   ├── PropertySearchTool.java
│   │   ├── ScheduleViewingTool.java
│   │   └── MortgageCalculatorTool.java
│   └── common/                 # Generic tools
│
├── prompt/                     # Prompt engineering
│   ├── PromptTemplate.java     # Variable substitution
│   ├── ChatPromptTemplate.java # Multi-message prompts
│   └── templates/              # Predefined prompts
│
├── controller/                 # REST API endpoints
├── service/auth/               # Authentication
├── security/                   # JWT, filters
├── model/                      # Entities, DTOs, enums
└── repository/                 # Data access
```

### Core Components

#### 1. Orchestration Layer (LangGraph-style)
- **StateGraph**: Define workflow as nodes and edges
- **GraphNode**: LLMNode, RAGNode, ToolNode, ConditionalNode
- **CompiledGraph**: Executable workflow with streaming support
- **Checkpointer**: State persistence for resumable workflows

#### 2. Chain Layer
- **LLMChain**: Simple prompt → LLM → response
- **RAGChain**: Query → Retrieve → Augment → Generate
- **RouterChain**: Classify intent and route to sub-chains
- **SequentialChain**: Execute chains in order
- **ParallelChain**: Execute chains concurrently

#### 3. Agent Layer
- **ReActAgent**: Thought → Action → Observation loop
- **PlanAndExecuteAgent**: Create plan, execute steps
- **ToolCallingAgent**: Structured function calling
- **MultiAgentCoordinator**: Route to specialized agents

#### 4. RAG Pipeline
- **Loaders**: PDF, DOCX, Web, Text, Directory
- **Splitters**: RecursiveCharacter, Semantic, Token-based
- **Retrievers**: Vector, BM25, Hybrid (RRF), MultiQuery
- **Pipeline**: Orchestrates ingest and query

#### 5. Memory Layer
- **ShortTermMemory**: Last N messages buffer
- **LongTermMemory**: Vector store for semantic recall
- **SummaryMemory**: LLM-generated conversation summary

### Security & Authentication

- **JWT-based authentication** via `JwtTokenProvider`, `JwtAuthenticationFilter`
- **Endpoints**: `/api/v1/auth/register`, `/api/v1/auth/login`, `/api/v1/auth/refresh`
- **Public endpoints**: Auth routes, Swagger UI, WebSocket, actuator health
- **Exception handling**: `GlobalExceptionHandler` with standardized `MyApiResponse<T>` format

### Data Model

#### Entities
- `AppUser` - Users with Spring Security integration
- `Conversation` - Chat sessions with status and metadata (JSONB)
- `Message` - Messages with role, tokens, latency, provider info
- `VectorChunkEntity` - Document chunks with embeddings

#### Key DTOs
- `ChatCompletionRequest/Response` - LLM interactions
- `RAGRequest/Response` - RAG queries
- `AgentRequest/AgentExecutionResponse` - Agent execution
- `WorkflowRequest/Response` - Graph execution

### Key Integrations

- **LLM Providers**: Google GenAI, Ollama, OpenAI, Anthropic
- **Vector Store**: PostgreSQL with pgvector extension
- **Embeddings**: Google GenAI, Ollama (nomic-embed-text)
- **Document Parsing**: Apache PDFBox, Apache POI, JSoup

## Configuration

Uses `application.yml` with environment variable overrides:

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_HOST` | PostgreSQL host | `localhost` |
| `DATABASE_PORT` | PostgreSQL port | `5432` |
| `DATABASE_NAME` | Database name | `chatbot_db` |
| `DATABASE_USERNAME` | Database username | `postgres` |
| `DATABASE_PASSWORD` | Database password | `postgres` |
| `JWT_SECRET` | Base64-encoded JWT signing key | (dev default) |
| `JWT_EXPIRATION_MS` | Access token expiry | `3600000` (1 hour) |
| `JWT_REFRESH_EXPIRATION_MS` | Refresh token expiry | `604800000` (7 days) |
| `GOOGLE_AI_API_KEY` | Google GenAI API key | - |
| `OLLAMA_BASE_URL` | Ollama server URL | `http://localhost:11434` |
| `OPENAI_API_KEY` | OpenAI API key | - |
| `ANTHROPIC_API_KEY` | Anthropic API key | - |

## Development Guidelines

### Adding a New LLM Provider
1. Create service in `service/llm/` extending `BaseLLMService`
2. Register with `@Service("providername")` annotation
3. Inject Spring AI model or implement HTTP client

### Adding a New Tool
1. Create class in `tool/` extending `BaseTool`
2. Define name, description, and JSON schema parameters
3. Implement `doExecute()` method
4. Register via `ToolRegistry`

### Adding a New Chain
1. Extend `BaseChain` in `chain/`
2. Implement `doInvoke()` method
3. Use `LLMService` and other chains as needed

### Adding a New Agent
1. Extend `BaseAgent` in `service/agent/`
2. Implement `executeLoop()` method
3. Define agent prompt and tool selection logic

### Creating a Workflow
```java
StateGraph<ChatGraphState> graph = new StateGraph<>();
graph.addNode("retrieve", new RAGNode<>(retriever, 5));
graph.addNode("generate", new LLMNode<>(llmService, template));
graph.addEdge("retrieve", "generate");
graph.setEntryPoint("retrieve");

CompiledGraph<ChatGraphState> compiled = graph.compile(checkpointer);
ChatGraphState result = compiled.invoke(initialState);
```
