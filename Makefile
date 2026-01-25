.PHONY: help build up down logs shell clean rebuild

# Default target
help:
	@echo "Real Estate Chatbot - Docker Commands"
	@echo "======================================"
	@echo ""
	@echo "Usage: make [target]"
	@echo ""
	@echo "Targets:"
	@echo "  build        Build Docker images (with layer caching)"
	@echo "  rebuild      Force rebuild without cache"
	@echo "  up           Start all services"
	@echo "  down         Stop all services"
	@echo "  logs         View logs (all services)"
	@echo "  logs-app     View app logs only"
	@echo "  shell        Open shell in app container"
	@echo "  clean        Remove containers, volumes, and images"
	@echo ""
	@echo "Required environment variables (.env file):"
	@echo "  GOOGLE_AI_API_KEY   - Google Gemini API key (for chat)"
	@echo "  COHERE_API_KEY      - Cohere API key (for embeddings) "
	@echo ""

# Build images with layer caching
build:
	docker compose build

# Force rebuild without cache
rebuild:
	docker compose build --no-cache

# Start all services
up:
	docker compose up -d

# Start with logs
up-logs:
	docker compose up

# Stop all services
down:
	docker compose down

# View all logs
logs:
	docker compose logs -f

# View app logs only
logs-app:
	docker compose logs -f app

# Open shell in app container
shell:
	docker compose exec app /bin/sh

# Pull required Ollama models
pull-models:
	@echo "Pulling Ollama models..."
	docker compose exec ollama ollama pull llama3.2
	docker compose exec ollama ollama pull nomic-embed-text
	@echo "Done! Models ready."

# Clean everything
clean:
	docker compose down -v --rmi local
	@echo "Cleaned up containers, volumes, and local images."

# Check status
status:
	docker compose ps

# View resource usage
stats:
	docker stats --no-stream