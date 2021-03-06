# Release Notes

### v3.0.0
- Initial release with Viscerion branding
- Allow switching between whitelist and blacklist for userspace backend (more below)
- Smaller install size
- Ensure notification reacts to tunnel changes from command line tools
- Disable sound and notification for wg-quick notification
- Fix some misleading error messages
- Ensure keyboard shows up for tunnel name when importing QR codes
- Faster layout load times
- Code cleanup across the board


When the whitelist option is enabled, all apps that are not in the exclusions
list are exempt from going through the tunnel, and the ones in the list are
the only ones which use the tunnel for networking.

### v2.0.0
- Rewrite entire codebase based on upstream remodel
- Future-proof Magisk version detection
- Fix restorecon issues with Magisk
- Ensure command line tools know the correct paths to probe for config files
- Update userspace implementation to work with Android 9's bionic restrictions
