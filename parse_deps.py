import os
import re

project_root = "/Users/jehadhamid/Documents/E-Mall/backend-clean"
src_dir = os.path.join(project_root, "src/main/java")

for root, _, files in os.walk(src_dir):
    for file in files:
        if file.endswith(".java"):
            file_path = os.path.join(root, file)
            rel_source_path = os.path.relpath(file_path, project_root)
            
            with open(file_path, 'r', encoding='utf-8') as f:
                for line in f:
                    line_stripped = line.strip()
                    if line_stripped.startswith("import store.emall.backend"):
                        import_match = re.match(r'import\s+([a-zA-Z0-9_\.]+);?', line_stripped)
                        if import_match:
                            imported_class = import_match.group(1)
                            # Convert package path to file path
                            dest_rel_path = "src/main/java/" + imported_class.replace(".", "/")
                            if not dest_rel_path.endswith("*"):
                                dest_rel_path += ".java"
                            
                            print(f"Source file: {rel_source_path}")
                            print(f"Destination file: {dest_rel_path}")
                            print(f"Exact code: {line_stripped}\n")
