# Thymeleaf PDF report CLI tool

CLI tool to help developers prepare PDF reports as quick as possible.
This tool takes html template, enrich it with data from json file and finally renders output to PDF.

Sample Usage (Linux):
```bash
cd thymeleaf-pdf-cli
cp sample-template/* templates/
./thymeleaf-pdf-cli gen -t index.html -d templates/data.json -o output.pdf
```

Sample Usage (Windows):
```bash
cd thymeleaf-pdf-cli
cp sample-template/* templates/
thymeleaf-pdf-cli.exe gen -t index.html -d templates/data.json -o output.pdf
```


### Developers note

Agent for graalvm native image:
`-agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image`