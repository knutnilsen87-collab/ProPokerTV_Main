# Repo Conventions

- package by feature
- DTOs in feature package
- service layer owns business rules
- repositories never returned directly from controllers
- soft delete for user-generated resources where reversible moderation is useful
- migrations required for schema change
