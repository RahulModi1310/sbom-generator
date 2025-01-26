
# SBOM Generator With Regex Search

## Steps to Run Docke Image
- Download the Docker Image from - [LINK](https://drive.google.com/file/d/142wEW84GBFH4oo_IgkACqbTXL3mG-gRC/view?usp=sharing)
- Load the Docker Image
```bash
   docker load --input .\sbomtool-image.tar
```
- Run the Image
```bash
   docker run sbomtool:latest --source <contianer-name> --search <regex-search query> --format {SPDX/CYCLONEDX} -o <output-file name>
```
- Copy the generated file to host pc
```bash
   docker cp <container-id>:app/<output-file name> .
```
