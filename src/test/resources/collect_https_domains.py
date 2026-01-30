#!/usr/bin/env python3
"""
Generated with ChatGPT 5.1

Collect all https:// URLs in .md/.markdown files of a folder (or file/glob) and print per-file
statistics showing how often each domain was referenced.

Usage:
  python collect_https_domains.py PATH [--recursive] [--strip-www] [--aggregate]

PATH may be:
 - a directory (relative or absolute)
 - a single .md/.markdown file
 - a glob pattern (e.g. "docs/**/*.md" or "docs/*.markdown")
 - omitted (defaults to current directory ".")

Examples:
  python collect_https_domains.py docs
  python collect_https_domains.py docs/subdir --recursive
  python collect_https_domains.py "docs/**/*.md" --aggregate --strip-www
"""
from pathlib import Path
import argparse
import re
from urllib.parse import urlparse
from collections import Counter
import sys
import os
import glob

URL_RE = re.compile(r"https://[^\s\)\]\>\'\"`]+", re.IGNORECASE)
TRAILING_PUNCT = ".,;:!?)]'`"
DEFAULT_EXTS = {".md", ".markdown"}


def find_md_files(root: Path, recursive: bool, exts):
    if recursive:
        # rglob for each extension to avoid scanning all files unnecessarily
        for ext in exts:
            yield from root.rglob(f"*{ext}")
    else:
        for p in root.iterdir():
            if p.is_file() and p.suffix.lower() in exts:
                yield p


def normalize_netloc(url: str, strip_www: bool):
    parsed = urlparse(url)
    netloc = parsed.netloc.lower()
    # Remove userinfo if present (user:pass@host)
    if "@" in netloc:
        netloc = netloc.split("@", 1)[1]
    # Remove port if present
    if ":" in netloc:
        netloc = netloc.split(":", 1)[0]
    if strip_www:
        netloc = re.sub(r"^www\.", "", netloc)
    return netloc


def strip_trailing_punct(url: str):
    # Remove common trailing punctuation introduced by prose/markdown wrapping
    while url and url[-1] in TRAILING_PUNCT:
        url = url[:-1]
    return url


def collect_domains_in_file(path: Path, strip_www: bool):
    try:
        text = path.read_text(encoding="utf-8", errors="ignore")
    except Exception:
        return Counter()
    matches = URL_RE.findall(text)
    domains = []
    for raw in matches:
        url = strip_trailing_punct(raw)
        try:
            domain = normalize_netloc(url, strip_www)
            if domain:
                domains.append(domain)
        except Exception:
            continue
    return Counter(domains)


def print_file_stats(path: Path, counter: Counter):
    total_links = sum(counter.values())
    unique_domains = len(counter)
    print(f"{path} — {total_links} https links, {unique_domains} unique domains")
    if total_links == 0:
        print()
        return
    for domain, cnt in counter.most_common():
        print(f"  {cnt:4d}  {domain}")
    print()


def print_aggregate(aggregate_counter: Counter):
    total_links = sum(aggregate_counter.values())
    unique_domains = len(aggregate_counter)
    print("AGGREGATE SUMMARY")
    print(f"Total https links: {total_links}, Unique domains: {unique_domains}")
    for domain, cnt in aggregate_counter.most_common():
        print(f"  {cnt:6d}  {domain}")
    print()


def gather_md_files(path_arg: str, recursive: bool, exts):
    # Expand ~ and environment variables
    path_arg = os.path.expanduser(os.path.expandvars(path_arg))

    # If the argument contains glob characters, expand via glob
    if any(ch in path_arg for ch in "*?[]"):
        matches = [Path(p) for p in glob.glob(path_arg, recursive=True)]
        md_files = []
        for m in matches:
            if m.is_file() and m.suffix.lower() in exts:
                md_files.append(m)
            elif m.is_dir():
                md_files.extend(list(find_md_files(m, recursive, exts)))
        return sorted(set(md_files))

    root = Path(path_arg)
    if not root.exists():
        raise FileNotFoundError(f"Path does not exist: {path_arg}")

    if root.is_file():
        return [root] if root.suffix.lower() in exts else []
    return sorted(find_md_files(root, recursive, exts))


def main(argv):
    ap = argparse.ArgumentParser(description="Collect https:// URLs in .md/.markdown files and print per-file domain statistics.")
    ap.add_argument("path", nargs="?", default=".", help="Folder/file/glob to search (default: current directory)")
    ap.add_argument("--recursive", "-r", action="store_true", help="Search subdirectories recursively (uses rglob)")
    ap.add_argument("--strip-www", "-w", action="store_true", help="Treat www.example.com as example.com")
    ap.add_argument("--aggregate", "-a", action="store_true", help="Print aggregate summary across all files")
    ap.add_argument("--extensions", "-e", nargs="+", default=[".md", ".markdown"],
                    help="File extensions to consider (default: .md .markdown)")
    args = ap.parse_args(argv)

    exts = {e if e.startswith(".") else f".{e}" for e in args.extensions}
    try:
        md_files = gather_md_files(args.path, args.recursive, exts)
    except FileNotFoundError as exc:
        print(exc, file=sys.stderr)
        sys.exit(2)

    if not md_files:
        print("No .md/.markdown files found.")
        return

    aggregate = Counter()
    for p in md_files:
        counter = collect_domains_in_file(p, args.strip_www)
        print_file_stats(p, counter)
        aggregate.update(counter)

    if args.aggregate:
        print_aggregate(aggregate)


if __name__ == "__main__":
    main(sys.argv[1:])