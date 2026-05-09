# Security Policy

## Supported Versions

bDoci is an active project. Security fixes are expected to land on the latest maintained code in the default branch.

If you discover a security issue, report it against the current codebase rather than assuming older snapshots are supported.

## Reporting a Vulnerability

Please do not open a public GitHub issue for security-sensitive problems.

Instead, report the issue privately to the maintainer:

- GitHub: [@Bimbok](https://github.com/Bimbok)

When reporting, include:

- A clear description of the issue
- Steps to reproduce
- Impact assessment
- Any proof-of-concept details that help validate the report
- Suggested remediation, if you have one

## What to Report

Examples of relevant security issues include:

- Deep-link import abuse or malformed payload handling
- QR share injection or unsafe parsing behavior
- Notification or intent spoofing issues
- Sensitive local data exposure
- Permission misuse involving overlay or foreground service behavior
- Insecure network handling or unsafe transport assumptions
- Unsafe handling of external content, imported payloads, or user-controlled input

## Response Process

The maintainer will review reports, confirm impact, and decide on remediation priority.

Where appropriate:

- The issue will be reproduced
- A fix will be prepared
- Public disclosure will happen after a patch is available or risk is understood

## Disclosure Expectations

Please avoid public disclosure until the maintainer has had a reasonable opportunity to investigate and address the issue.
