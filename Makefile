NIX := $(shell command -v nix 2>/dev/null)
DIRENV := $(shell command -v direnv 2>/dev/null)

.ONESHELL:

.PHONY: shell
shell: flake.nix flake.lock
ifndef NIX
	$(error "Nix is not installed in your system!")
endif
ifdef IN_NIX_SHELL
	devenv up
else
	$(NIX) develop --impure
endif

.PHONY: shell-direnv
shell-direnv: .envrc
ifndef DIRENV
	$(error "Direnv is not installed in your system!")
endif
ifndef NIX
	$(error "Nix is not installed in your system!")
endif
	$(DIRENV) allow

.PHONY: reload-shell
reload-shell: flake.nix flake.lock
ifndef IN_NIX_SHELL
	$(error "You're not inside a Nix Shell, please run `make shell` first")
endif
	devenv up

.PHONY: test-temporal-connection
test-temporal-connection:
	tctl cluster health

.PHONY: create-namespace
create-namespace:
	tctl --namespace $(TEMPORAL_NAMESPACE) namespace register
