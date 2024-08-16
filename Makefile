lint:
    docker run -v $(pwd)/app:/app --entrypoint ktlint --rm -it kkopper/ktlint -F /app
