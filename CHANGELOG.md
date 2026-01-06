# Changelog

All notable changes to this project will be documented in this file.

## [1.3] - 2026-01-07

### Changed
- **Migrated item management to ModMenu UI**: Removed `/chomagerie shulkerrefill items` commands in favor of a graphical interface
  - Added dropdown selector for common items (60+ items)
  - Added custom item text field for adding any item
  - Added reset option to allow all items
  - Items are now managed through the ModMenu configuration screen instead of commands

- **Enhanced message customization**:
  - Added intuitive color selector with 16 Minecraft colors + 5 text styles (bold, strikethrough, underline, italic, obfuscated)
  - Added live preview of the message with applied formatting
  - Color codes are now visually selectable instead of manual text input

### Removed
- `/chomagerie shulkerrefill items add <itemId>` command
- `/chomagerie shulkerrefill items remove <itemId>` command
- `/chomagerie shulkerrefill items list` command
- `/chomagerie shulkerrefill items clear` command

### Added
- `ColorCodeHelper.java`: Utility class for managing Minecraft formatting codes intuitively
- `ItemSelectionHelper.java`: Helper class for managing available items in the game
- Enhanced ModMenuIntegration with new UI controls for items and message customization

