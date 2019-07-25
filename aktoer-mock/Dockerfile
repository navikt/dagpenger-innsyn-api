FROM node:10

WORKDIR /src

COPY package*.json ./

RUN npm install

COPY . .

EXPOSE 3050
CMD ["node", "server.js"]

