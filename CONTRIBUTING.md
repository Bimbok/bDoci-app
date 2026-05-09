# Contributing to bDoci

Thanks for contributing to bDoci.

bDoci values small, focused, technically clear contributions over broad, unclear changes. Improvements to the Android experience, offline behavior, UI polish, and developer workflow are all useful contributions.

## Before You Start

- Check existing issues and pull requests before starting work.
- Check [SUPPORT.md](SUPPORT.md) if you need help before contributing.
- Open an issue first for large features, architectural changes, or behavioral changes.
- Keep changes scoped. Avoid mixing refactors, feature work, and unrelated formatting in one PR.

## Local Setup

### Requirements

- Android Studio
- JDK 11+
- Android SDK for API 27 or above
- Firebase project configuration if you want to test push notifications

### Setup

1. Fork and clone the repository.
2. Open the project in Android Studio.
3. Add `google-services.json` inside `app/` if you need Firebase-backed features.
4. Let Gradle sync fully.
5. Run the app on an emulator or device.

## Contribution Workflow

1. Fork the repository.
2. Create a branch from `main`.
3. Make your change.
4. Test the affected behavior.
5. Open a pull request with a clear description.

## Branch Naming

Use simple, descriptive branch names such as:

- `feature/floating-ui-improvements`
- `fix/qr-import-crash`
- `docs/readme-refresh`

## Commit Messages

Use concise commit messages that explain the intent of the change.

Examples:

- `fix: handle empty QR import payload`
- `feat: improve floating panel layout`
- `docs: add security policy`

## Pull Request Guidelines

Each pull request should include:

- A short summary of what changed
- Why the change was needed
- Screenshots or screen recordings for UI changes, if applicable
- Test notes describing what was verified
- A passing PR check workflow

## Code Guidelines

- Keep Kotlin code readable and lifecycle-aware.
- Follow the existing MVVM and repository structure.
- Prefer targeted fixes over broad rewrites unless a rewrite is justified.
- Do not introduce unrelated dependency churn.
- Keep XML layouts maintainable and consistent with the current visual language.

## Testing Expectations

Before opening a PR, verify as much of the affected behavior as possible:

- Dashboard loading and filtering
- Document detail rendering
- Favorites persistence
- QR generation/import
- Floating overlay behavior
- Offline fallback behavior

If you could not test something, say so explicitly in the pull request.

## Documentation Changes

Update documentation when your change affects:

- Setup steps
- Permissions
- Firebase configuration
- Deep-link behavior
- User-facing features

## Security

Do not commit secrets, API keys, signing keys, or private configuration files.

In particular:

- Do not commit `google-services.json` unless it is a safe placeholder
- Do not commit production credentials
- Report security concerns privately where appropriate

See [SECURITY.md](SECURITY.md) for vulnerability reporting guidance.

## Conduct

By contributing, you agree to follow the project [Code of Conduct](CODE_OF_CONDUCT.md) and [Security Policy](SECURITY.md) where applicable.
