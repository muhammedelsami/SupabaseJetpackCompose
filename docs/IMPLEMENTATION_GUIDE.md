# Step-by-Step Implementation Guide

## 1. Base Setup
1. Create project with Compose + Material 3.
2. Add Hilt, Navigation, DataStore, Serialization, Coil, Ktor dependencies.
3. Enable `buildConfig` and inject Supabase keys from `gradle.properties`.

## 2. Layering
1. Define domain repository contracts.
2. Add use cases for auth and note actions.
3. Implement data repositories.

## 3. Authentication
1. Register/login/forgot password actions.
2. Persist session in DataStore.
3. Auto-login by checking existing session at startup.
4. Logout and delete account clear session.

## 4. Notes CRUD
1. Create note with title/content/image metadata.
2. Read user notes (filtered by `user_id`).
3. Update title/content/image.
4. Delete note and linked image.

## 5. Realtime
1. Add `notes` table to realtime publication.
2. Subscribe to user-scoped changes.
3. Reflect insert/update/delete instantly in UI state.

## 6. Storage
1. Create `notes-images` private bucket.
2. Upload images under `<user_id>/<note_id>/`.
3. Replace image by deleting old object then uploading new one.

## 7. Profile & Account Deletion
1. Show user info.
2. Confirm delete action.
3. Delete all user notes/images.
4. Delete auth user and clear local session.

## 8. QA & Hardening
1. Add unit tests for ViewModels/use cases.
2. Add instrumentation tests for auth + notes journeys.
3. Add strict lint/ktlint/detekt in CI.
