const fs = require('fs');
const path = require('path');

const outputFile = path.join(__dirname, 'full.cr.txt');

fs.writeFileSync(outputFile, '');

function processFile(filePath) {
  const relativePath = path.relative(__dirname, filePath);
  const fileContent = fs.readFileSync(filePath, 'utf-8');

  const fileData = `====================
FILE: ${relativePath}

${fileContent}
`;

  fs.appendFileSync(outputFile, fileData);
}

function traverseDirectory(directory) {
  const files = fs.readdirSync(directory);

  files.forEach(file => {
    const fullPath = path.join(directory, file);
    const stat = fs.statSync(fullPath);

    if (stat.isDirectory()) {
      traverseDirectory(fullPath);
    } else if (path.extname(fullPath) === '.java') {
      processFile(fullPath);
    }
  });
}

traverseDirectory(__dirname);

console.log(`Full content has been written to ${outputFile}`);
