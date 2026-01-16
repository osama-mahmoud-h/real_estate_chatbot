# Real Estate AI Chatbot

A production-ready AI-powered chatbot for real estate applications built with **Spring Boot 3.5**, **Spring AI 1.1.2**, and a **LangGraph-style architecture**. Features RAG (Retrieval-Augmented Generation), autonomous agents, composable chains, and multi-provider LLM support.

## Features

- **LangGraph-style Orchestration**: State-based workflow graphs with conditional routing
- **RAG Pipeline**: Document ingestion, chunking, embedding, and hybrid retrieval
- **Autonomous Agents**: ReAct, Plan-and-Execute, and Tool-calling agents
- **Multi-LLM Support**: Google GenAI, Ollama, OpenAI, Anthropic
- **Vector Search**: PostgreSQL with pgvector for semantic search
- **Conversation Memory**: Short-term, long-term, and summary-based memory
- **Real Estate Tools**: Property search, viewing scheduler, mortgage calculator
- **JWT Authentication**: Secure API with role-based access control
- **Streaming Support**: SSE and WebSocket for real-time responses

## Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              API GATEWAY LAYER                               │
│  REST Controllers  │  WebSocket (Real-time)  │  SSE Streaming               │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           ORCHESTRATION LAYER                                │
│  ┌────────────────────────────────────────────────────────────────────────┐ │
│  │                     StateGraph (LangGraph-style)                       │ │
│  │  Nodes (LLM, RAG, Tool, Conditional)  │  Edges  │  Checkpointer        │ │
│  └────────────────────────────────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────────────────────────────┐ │
│  │                          Chain Orchestrator                            │ │
│  │  LLMChain  │  RAGChain  │  RouterChain  │  SequentialChain             │ │
│  └────────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
          ┌───────────────────────────┼───────────────────────────┐
          ▼                           ▼                           ▼
┌──────────────────┐    ┌──────────────────────┐    ┌──────────────────────┐
│   AGENT LAYER    │    │     RAG PIPELINE     │    │    MEMORY LAYER      │
│ ReActAgent       │    │ Loaders (PDF, Web)   │    │ ShortTermMemory      │
│ PlanExecuteAgent │    │ Splitters (Semantic) │    │ LongTermMemory       │
│ ToolCallingAgent │    │ Retrievers (Hybrid)  │    │ SummaryMemory        │
│ MultiAgent       │    │ VectorStore          │    │                      │
└──────────────────┘    └──────────────────────┘    └──────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                            LLM PROVIDER LAYER                                │
│         LLMFactory → GoogleGenAI │ Ollama │ OpenAI │ Anthropic              │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           INFRASTRUCTURE LAYER                               │
│  PostgreSQL  │  PGVector (Embeddings)  │  Redis (Cache)  │  Message Queue  │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Tech Stack

| Layer | Technology |
|-------|------------|
| Framework | Spring Boot 3.5.9, Java 17 |
| AI/ML | Spring AI 1.1.2 |
| LLM Providers | Google GenAI, Ollama, OpenAI, Anthropic |
| Vector Store | PostgreSQL + pgvector |
| Security | Spring Security, JWT (JJWT 0.12.6) |
| Streaming | Spring WebFlux, WebSocket |
| Document Parsing | Apache PDFBox, Apache POI, JSoup |
| API Docs | SpringDoc OpenAPI 2.1.0 |
| Build | Maven |

## Project Structure

```
src/main/java/semsem/chatbot/
├── orchestration/          # LangGraph-style workflow engine
│   ├── graph/              # StateGraph, CompiledGraph, GraphState
│   ├── node/               # LLMNode, RAGNode, ToolNode, RouterNode
│   ├── checkpointer/       # InMemoryCheckpointer, PostgresCheckpointer
│   └── executor/           # GraphExecutor
│
├── chain/                  # Composable LLM chains
│   ├── LLMChain            # Simple LLM invocation
│   ├── RAGChain            # Retrieval-Augmented Generation
│   ├── RouterChain         # Intent-based routing
│   ├── SequentialChain     # Chain of chains
│   └── ConversationChain   # With memory support
│
├── service/agent/          # Autonomous AI agents
│   ├── ReActAgent          # Reasoning + Acting loop
│   ├── PlanAndExecuteAgent # Plan then execute
│   ├── ToolCallingAgent    # Function calling
│   └── MultiAgentCoordinator
│
├── rag/                    # RAG pipeline
│   ├── loader/             # PDF, DOCX, Web, Text loaders
│   ├── splitter/           # Recursive, Semantic, Token splitters
│   ├── retriever/          # Vector, BM25, Hybrid, MultiQuery
│   └── pipeline/           # RAGPipeline orchestration
│
├── vectorstore/            # Vector database
│   ├── VectorStore         # Interface
│   ├── PGVectorStore       # PostgreSQL pgvector
│   └── SpringAIVectorStore # Spring AI integration
│
├── service/
│   ├── llm/                # LLM providers (Google, Ollama, OpenAI, Anthropic)
│   ├── embedding/          # Embedding services
│   ├── memory/             # Conversation memory
│   ├── auth/               # Authentication
│   └── chat/               # Chat services
│
├── tool/                   # Agent tools
│   ├── realestate/         # PropertySearch, ScheduleViewing, MortgageCalc
│   └── common/             # WebSearch, Calculator
│
├── prompt/                 # Prompt templates
│   ├── templates/          # RAG, Agent, Chat prompts
│   └── builder/            # PromptBuilder, FewShotBuilder
│
├── controller/             # REST API
├── security/               # JWT authentication
├── model/                  # Entities, DTOs
└── repository/             # Data access
```

## What's Completed

### Core Infrastructure
- [x] Spring Boot 3.5.9 project setup
- [x] JWT authentication (register, login, refresh)
- [x] PostgreSQL database integration
- [x] Global exception handling
- [x] Swagger/OpenAPI documentation
- [x] Conversation & Message management APIs

### Architecture (Skeleton)
- [x] **Orchestration Layer**: StateGraph, GraphNode, GraphEdge, CompiledGraph, Checkpointer
- [x] **Chain Layer**: Chain, LLMChain, RAGChain, RouterChain, SequentialChain, ParallelChain
- [x] **Agent Layer**: Agent, ReActAgent, PlanAndExecuteAgent, ToolCallingAgent, MultiAgentCoordinator
- [x] **RAG Pipeline**: DocumentLoader, TextSplitter, Retriever, RAGPipeline
- [x] **Vector Store**: VectorStore interface, PGVectorStore, SpringAIVectorStore
- [x] **Embedding**: EmbeddingService, Google/Ollama implementations
- [x] **Memory**: ShortTermMemory, LongTermMemory, SummaryMemory
- [x] **LLM Providers**: LLMService, LLMFactory, Google/Ollama/OpenAI/Anthropic services
- [x] **Tools**: Tool interface, ToolRegistry, Real estate tools
- [x] **Prompts**: PromptTemplate, ChatPromptTemplate, predefined templates

### DTOs & Models
- [x] ChatCompletionRequest/Response
- [x] RAGRequest/Response
- [x] AgentRequest/AgentExecutionResponse
- [x] WorkflowRequest/Response
- [x] VectorChunkEntity

## What's TODO

### High Priority
- [ ] **Implement LLM Services**: Wire up Spring AI ChatModel for each provider
- [ ] **Implement RAG Pipeline**: Complete loader, splitter, retriever logic
- [ ] **Implement VectorStore**: Add pgvector operations with native queries
- [ ] **Implement Embedding Services**: Connect to Spring AI EmbeddingModel
- [ ] **Enable Spring AI Dependencies**: Uncomment in pom.xml

### Medium Priority
- [ ] **Implement StateGraph Execution**: Complete graph traversal logic
- [ ] **Implement Chain Logic**: Add prompt formatting and LLM calls
- [ ] **Implement Agent Loops**: ReAct, Plan-and-Execute patterns
- [ ] **Implement Memory Persistence**: Save/load conversation history
- [ ] **Add Streaming Support**: SSE endpoints for token streaming

### Lower Priority
- [ ] **Add Redis Caching**: Cache embeddings and LLM responses
- [ ] **Add Rate Limiting**: Bucket4j for API throttling
- [ ] **Add Circuit Breaker**: Resilience4j for LLM failover
- [ ] **Add Metrics**: Micrometer for observability
- [ ] **Add Message Queue**: Async document ingestion
- [ ] **Add WebSocket Chat**: Real-time chat interface
- [ ] **Add Reranking**: Cross-encoder reranking for retrieval

### Database
- [ ] **Create pgvector Migration**: Add vector_chunks table with HNSW index
- [ ] **Add Full-Text Search Index**: For hybrid retrieval

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL 15+ with pgvector extension
- (Optional) Ollama for local LLM

### Database Setup

```sql
-- Enable pgvector extension
CREATE EXTENSION IF NOT EXISTS vector;

-- Create vector chunks table
CREATE TABLE vector_chunks (
                               id VARCHAR(36) PRIMARY KEY,
                               document_id VARCHAR(36) NOT NULL,
                               content TEXT NOT NULL,
                               chunk_index INTEGER,
                               embedding vector(768),
                               metadata JSONB,
                               created_at TIMESTAMP DEFAULT NOW()
);

-- Create HNSW index for fast similarity search
CREATE INDEX idx_vector_chunks_embedding
    ON vector_chunks USING hnsw (embedding vector_cosine_ops);
```

### Configuration

Create `.env` file or set environment variables:

```env
DATABASE_HOST=localhost
DATABASE_PORT=5432
DATABASE_NAME=chatbot_db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres

JWT_SECRET=your-base64-encoded-secret-key

# LLM Providers (at least one required)
GOOGLE_AI_API_KEY=your-google-api-key
OLLAMA_BASE_URL=http://localhost:11434
OPENAI_API_KEY=your-openai-key
ANTHROPIC_API_KEY=your-anthropic-key
```

### Build & Run

```bash
# Build
./mvnw clean compile

# Run
./mvnw spring-boot:run

# Test
./mvnw test

# Package
./mvnw package
```

### API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/auth/register` | POST | User registration |
| `/api/v1/auth/login` | POST | User login |
| `/api/v1/auth/refresh` | POST | Refresh token |
| `/api/v1/conversations` | GET/POST | List/Create conversations |
| `/api/v1/conversations/{id}` | GET/PUT/DELETE | Manage conversation |
| `/api/v1/conversations/{id}/messages` | GET/POST | List/Create messages |
| `/swagger-ui.html` | GET | API documentation |

## Usage Examples

### Create a RAG Workflow

```java
// Define the workflow graph
StateGraph<ChatGraphState> graph = new StateGraph<>();

// Add nodes
graph.addNode("retrieve", new RAGNode<>(retriever, 5));
        graph.addNode("generate", new LLMNode<>(llmService, ragPrompt));

// Add edges
        graph.addEdge("retrieve", "generate");
graph.setEntryPoint("retrieve");
graph.setFinishPoint("generate");

// Compile and execute
CompiledGraph<ChatGraphState> compiled = graph.compile(checkpointer);
ChatGraphState result = compiled.invoke(ChatGraphState.builder()
        .userQuery("Find apartments in downtown")
        .conversationId("conv-123")
        .build());
```

### Use a Chain

```java
RAGChain ragChain = RAGChain.builder()
        .retriever(hybridRetriever)
        .llmService(llmService)
        .promptTemplate(RAGPrompts.CONTEXT_QA)
        .topK(5)
        .build();

ChainResult result = ragChain.invoke(Map.of(
        "question", "What properties are available under $500k?"
));
```

### Execute an Agent

```java
ReActAgent agent = ReActAgent.builder()
        .llmService(llmService)
        .reactPromptTemplate(AgentPrompts.REACT_SYSTEM)
        .build();

agent.addTool(propertySearchTool);
agent.addTool(mortgageCalculatorTool);

AgentResponse response = agent.run(
        "Find me a 3-bedroom house and calculate the mortgage for a 30-year loan"
);
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [LangChain](https://langchain.com/) - Inspiration for chain architecture
- [LangGraph](https://github.com/langchain-ai/langgraph) - Inspiration for graph-based orchestration
- [Spring AI](https://spring.io/projects/spring-ai) - AI/ML integration for Spring
