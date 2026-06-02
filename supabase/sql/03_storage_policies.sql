insert into storage.buckets (id, name, public)
values ('notes-images', 'notes-images', true)
on conflict (id) do update set public = true;

drop policy if exists "notes_images_select_own" on storage.objects;
create policy "notes_images_select_own"
on storage.objects
for select
using (
  bucket_id = 'notes-images'
  and auth.uid()::text = (storage.foldername(name))[1]
);

drop policy if exists "notes_images_insert_own" on storage.objects;
create policy "notes_images_insert_own"
on storage.objects
for insert
with check (
  bucket_id = 'notes-images'
  and auth.uid()::text = (storage.foldername(name))[1]
);

drop policy if exists "notes_images_update_own" on storage.objects;
create policy "notes_images_update_own"
on storage.objects
for update
using (
  bucket_id = 'notes-images'
  and auth.uid()::text = (storage.foldername(name))[1]
)
with check (
  bucket_id = 'notes-images'
  and auth.uid()::text = (storage.foldername(name))[1]
);

drop policy if exists "notes_images_delete_own" on storage.objects;
create policy "notes_images_delete_own"
on storage.objects
for delete
using (
  bucket_id = 'notes-images'
  and auth.uid()::text = (storage.foldername(name))[1]
);
