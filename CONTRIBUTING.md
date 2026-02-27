# Contributing to Kord

First off, thank you for considering contributing to Kord! It's
people like you that make Kord such a great tool.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Setup](#development-setup)
- [Pull Request Process](#pull-request-process)
- [Style Guide](#style-guide)
- [Community](#community)

## Code of Conduct

This project and everyone participating in it is governed by our
[Code of Conduct](https://github.com/kordlib/.github/blob/main/.github/CODE_OF_CONDUCT.md). By participating, you are
expected to uphold this code.

## How Can I Contribute?

### 🐛 Reporting Bugs

Before creating bug reports, please check existing issues to avoid
duplicates. When you create a bug report, include as many details as
possible using our [bug report template](.github/ISSUE_TEMPLATE/bug_report.yml).

**Great bug reports include:**
- A clear, descriptive title
- Steps to reproduce the behavior
- Expected behavior vs actual behavior
- Screenshots (if applicable)
- Environment details (Kord version, Kotlin version, etc.)

### 💡 Suggesting Features

Feature requests are welcome! Please use our
[feature request template](.github/ISSUE_TEMPLATE/feature_request.yml).

**Great feature requests include:**
- Clear problem statement: "I'm frustrated when..."
- Proposed solution
- Alternative solutions you've considered
- Additional context

### 📝 Improving or Writing Documentation

Documentation contributions are always welcome!

Kord is currently in need of more documentation to its source code. Most people generally use the `core` package the
most in their project, so it's always a good place to start.

Beyond that we're always looking for:
- Fixing Typos
- Clarifying confusing sections of code or documentation
- Adding examples

### 🔧 Submitting Code

Look for issues labeled `good first issue` or `help wanted` for
great places to start.

## Development Setup

### Prerequisites

- Git
- Gradle
- Kotlin capable IDE

### Getting Started

```bash
# 1. Fork the repository on GitHub

# 2. Clone your fork locally
git clone https://github.com/YOUR_USERNAME/kord.git
cd [project-name]

# 3. Add upstream remote
git remote add upstream https://github.com/kordlib/kord.git

# 4. Load gradle
# Refresh gradle in your IDE to install the dependencies and load the project

# 5. Create a branch for your changes
git checkout -b feature/your-feature-name

# 6. Make your changes!
```

### Common Commands

| Command                     | Description       |
|-----------------------------|-------------------|
| `./gradlew build`           | Build the project |
| `./gradlew check`           | Run test suites   |
| `./gradlew updateLegacyAbi` | Update the API    |

## Pull Request Process

### Before Submitting

1. **Update your branch** with the latest upstream changes:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Run the full test suite** and ensure all tests pass:
   ```bash
   ./gradlew check
   ```
3. **Update the API**
   ```bash
   ./gradlew updateLegacyAbi
   ```

4. **Update documentation** if you've changed APIs or added features.

### Submitting

1. Push your branch to your fork:
   ```bash
   git push origin feature/your-feature-name
   ```

2. Open a Pull Request against the `main` branch.

3. Fill out the PR template completely.

4. Wait for review. We aim to respond within 7 days.

### PR Checklist


- [ ] I have checked for any PRs that already exist for this issue.
- [ ] I have followed
  the [contributor guidelines](https://github.com/kordlib/kord/blob/main/CONTRIBUTING.md).
- [ ] I have tested my code to the best of my ability.

## Style Guide

### Code Style

- Follow the `.editorconfig`
- Write self-documenting code with meaningful variable names
- Add KDoc comments for public APIs

## Community

- [Discord](https://discord.gg/6jcx5ev) - Chat with maintainers
- [Discussions](https://github.com/kordlib/kord/discussions) - Ask questions

---

Thank you for contributing! 🎉
