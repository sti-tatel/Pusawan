import os
import re

# Run this script from inside your Pusawan java source folder.
# It strips comments and concatenates all .java files into one file: bundled.txt

def strip_comments(code):
    # Remove block comments /* ... */
    code = re.sub(r'/\*.*?\*/', '', code, flags=re.DOTALL)
    # Remove line comments //
    code = re.sub(r'//[^\n]*', '', code)
    # Remove blank lines
    lines = [line for line in code.splitlines() if line.strip()]
    return '\n'.join(lines)

script_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "Pusawan-main", "Pusawan")
output_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "bundled.txt")

with open(output_path, 'w', encoding='utf-8') as out:
    for filename in sorted(os.listdir(script_dir)):
        if not filename.endswith('.java'):
            continue
        filepath = os.path.join(script_dir, filename)
        with open(filepath, 'r', encoding='utf-8') as f:
            code = f.read()
        stripped = strip_comments(code)
        out.write(f'// === {filename} ===\n')
        out.write(stripped)
        out.write('\n\n')

print(f'Done. Output: {output_path}')
