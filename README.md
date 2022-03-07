clone repository
```bash
git clone https://github.com/ZooMMaXDecentralNetwork/messenger-server
```
make project

```bash
cd messenger-server
mvn package && mkdir ../DecentralNetwork && cp target/MessangerServer-1.0.jar ../DecentralNetwork/MessangerServer-1.0.jar
```

start server

```bash
cd ../DecentralNetwork
java -jar MessangerServer-1.0.jar 185.235.131.192
```
