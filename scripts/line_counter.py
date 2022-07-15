#!/usr/bin/env python3
#
# Count lines of code in a codebase
#
# (c) Justus Languell 2022

import os

only_torquelib = False

languages = {
    'Java': '.java',
    'Python': '.py',
}

counts = {}

def count(path):
    if only_torquelib and 'torquelib' not in path:
        return
    source = open(path, 'r').read()
    ext = os.path.splitext(path)[-1]
    if ext not in counts:
        counts[ext] = 0
    counts[ext] += len(source.split('\n'))
    
if __name__ == '__main__':
    for root, subdirs, files in os.walk(os.path.realpath('./src/main/java/org/texastorque')):
        [count(root + '/' + file) for file in files]
    for lang, ext in languages.items():
        lines = counts[ext]
        print(f'{lang}: {lines} lines = ~{max(lines // 46, 1)} pages') if lines > 0 else None