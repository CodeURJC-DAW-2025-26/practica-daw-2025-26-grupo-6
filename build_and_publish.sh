cd ./frontend && npm run build
cd ..

mkdir -p ./backend/src/main/resources/static/new/
rm -rf ./backend/src/main/resources/static/new/*
cp -r ./frontend/build/client/* ./backend/src/main/resources/static/new/

mkdir -p ./backend/node/public/new/
rm -rf ./backend/node/public/new/*
cp -r ./frontend/build/client/* ./backend/node/public/new/