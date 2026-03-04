# GGO — Build & Deploy
# Usage: make [target] [VAR=value]

APP_NAME    := ggo
REGISTRY    := registry.oglimmer.com
IMAGE       := $(REGISTRY)/$(APP_NAME)
DEPLOY_NAME := $(APP_NAME)
NAMESPACE   := default
VERSION     = $(shell mvn help:evaluate -Dexpression=project.version -q -DforceStdout 2>/dev/null)

# Auto-detect platform
ARCH := $(shell uname -m)
ifeq ($(ARCH),x86_64)
  PLATFORM := linux/amd64
else ifeq ($(ARCH),arm64)
  PLATFORM := linux/arm64
else ifeq ($(ARCH),aarch64)
  PLATFORM := linux/arm64
else
  PLATFORM := linux/amd64
endif

.PHONY: build push deploy release show clean help

help: ## Show this help
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}'

build: ## Build Docker image
	docker buildx build --platform $(PLATFORM) -t $(IMAGE):latest --load .

push: ## Push image to registry
	docker push $(IMAGE):latest

deploy: ## Restart Kubernetes deployment
	kubectl rollout restart deployment/$(DEPLOY_NAME) -n $(NAMESPACE)

all: build push deploy ## Build, push, and deploy

show: ## Show current project version
	@echo "Version: $(VERSION)"

release: ## Create a release (usage: make release NEW_VERSION=1.2.3)
	@test -n "$(NEW_VERSION)" || (echo "Usage: make release NEW_VERSION=x.y.z" && exit 1)
	mvn versions:set -DnewVersion=$(NEW_VERSION) -q
	mvn versions:commit -q
	git add pom.xml
	git commit -m "Release v$(NEW_VERSION)"
	git tag "v$(NEW_VERSION)"
	$(MAKE) build push deploy
	@# Prepare next snapshot
	$(eval NEXT := $(shell echo $(NEW_VERSION) | awk -F. '{print $$1"."$$2"."$$3+1"-SNAPSHOT"}'))
	mvn versions:set -DnewVersion=$(NEXT) -q
	mvn versions:commit -q
	git add pom.xml
	git commit -m "Prepare next development iteration $(NEXT)"
	@echo "Released v$(NEW_VERSION), next dev version: $(NEXT)"
