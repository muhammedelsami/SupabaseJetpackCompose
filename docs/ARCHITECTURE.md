# Architecture Documentation

## Overview

The app follows a layered MVVM architecture:
- Presentation: Compose UI + ViewModels + UiState/Event contracts
- Domain: repository interfaces and use cases
- Data: repository implementations, local persistence, remote adapters

## MVVM Rules Applied

- Business logic is in ViewModels/use cases.
- UI consumes immutable `StateFlow`.
- One-way data flow:
  - UI event -> ViewModel -> UseCase/Repository -> State update -> UI render

## Repository Pattern

- `AuthRepository` abstracts auth/session/profile actions.
- `NotesRepository` abstracts notes CRUD, storage and realtime.
- Data source switching (mock/real Supabase) happens in data layer only.

## Navigation Flow

- Splash -> Auth -> Main (bottom nav)
- Main tabs:
  - Notes
  - Profile

## Error Handling

`Resource<T>`:
- `Loading`
- `Success(data)`
- `Error(message, throwable)`

This keeps API, DB, storage, and realtime errors uniform in UI.

## State Management

- `MutableStateFlow` in ViewModels
- `collectAsStateWithLifecycle()` in Compose
- no direct business mutation in composables

## Security Best Practices

- Keys are injected through `BuildConfig` from `gradle.properties`
- RLS enforced on notes
- Private bucket with owner-scoped storage policies
- Input validation in auth/note flows
- Session persisted via DataStore (no plaintext custom files)
