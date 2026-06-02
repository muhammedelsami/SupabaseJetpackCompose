# SupabaseJetpackCompose (Jetpack Compose + Supabase)

Production-oriented Android Notes application template with:
- Kotlin + Jetpack Compose (Material 3)
- MVVM + Repository + Clean Architecture style layering
- Hilt dependency injection
- Navigation Compose
- DataStore session persistence
- Supabase-ready SQL/RLS/Storage policy scripts

## 1) Project Structure

`com.muhammed.supabasejetpackcompose`

- `data`
  - `local`: DataStore session
  - `model`: DTO/domain-adjacent models
  - `repository`: repository implementations
- `domain`
  - `repository`: contracts
  - `usecase`: business actions
- `presentation`
  - `auth`
  - `notes`
  - `profile`
  - `navigation`
- `di`: Hilt modules
- `util`: shared classes (`Resource`)

## 2) Supabase Setup

1. Create a Supabase project (Free tier).
2. Run SQL files in this order:
   - `supabase/sql/01_notes_schema.sql`
   - `supabase/sql/02_rls_and_policies.sql`
   - `supabase/sql/03_storage_policies.sql`
3. Enable Email Auth in Supabase Auth.
4. Add redirect URL for password reset:
   - `notesapp://reset-password`
5. Fill local `gradle.properties`:
   - `SUPABASE_URL=https://<project-ref>.supabase.co`
   - `SUPABASE_ANON_KEY=<anon-key>`
   - `SUPABASE_REDIRECT_SCHEME=notesapp`

## 3) Realtime Flow

- Publish `public.notes` in `supabase_realtime`.
- Subscribe by `user_id` channel on client.
- Handle insert/update/delete events to refresh local UI state.

## 4) Storage Flow

- Bucket: `notes-images` (private)
- Object path convention:
  - `<user_id>/<note_id>/<filename>`
- On note delete:
  - remove image first
  - remove note row second

## 5) Build & Run

```bash
./gradlew assembleDebug
```

## 6) Security Notes

- Keep keys in `gradle.properties` (never hardcode).
- RLS enabled for all note CRUD operations.
- Storage access restricted to authenticated owner folder.
- Validate all user input in ViewModel/use case layer.

## 7) Current Status

This repository contains a complete architectural baseline and all Supabase SQL/policy assets required for production hardening.  
If you want, I can continue with:
- full Supabase SDK-backed repositories (Auth/PostgREST/Storage/Realtime),
- image picker/crop and replacement flow,
- detail/edit note screens with full nav graph routes,
- instrumentation/unit tests and CI setup.
