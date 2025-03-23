from doit.tools import Interactive

DOIT_CONFIG = {
    'backend': 'json',
    'dep_file': 'doit-db.json',
}


def success() -> str:
    return "echo \033[92mSuccess\033[97m"


def version_arg(default="*"):
    return {
        "name": "version",
        "short": "v",
        "long": "version",
        "choices": (("*", ""), ("0.0.1", "")),
        "type": str,
        "default": default,
        "help": "Package version",
    }


def mode_arg(default="fore"):
    return {
        "name": "mode",
        "short": "m",
        "long": "mode",
        "choices": (("back", ""), ("fore", "")),
        "type": str,
        "default": default,
        "help": "Start in the background or foreground",
    }


def tests_arg(default="true"):
    return {
        "name": "tests",
        "short": "t",
        "long": "tests",
        "choices": (("true", ""), ("false", "")),
        "type": str,
        "default": default,
        "help": "Execute or skip tests",
    }


def ssl_arg(default="false"):
    return {
        "name": "ssl",
        "short": "s",
        "long": "ssl",
        "choices": (("true", ""), ("false", "")),
        "type": str,
        "default": default,
        "help": "Enable TLS for http and web-socket proto",
    }


def port_arg(default=9087):
    return {
        "name": "port",
        "short": "p",
        "long": "port",
        "type": int,
        "default": default,
        "help": "Set port for service",
    }


def task_build_images():
    """Build docker container development:latest"""
    def build():
        cmd = f'bash -c "docker build --pull --rm -f Dockerfile -t development:latest ."'
        return cmd
    return {
        'actions': [Interactive(build), Interactive(success)],
    }


def task_container():
    """Run docker container development:latest. ONLY FOR FIRST ATTACH! AFTER USE `doit attach_container`"""
    def cleanup():
        cmd = f'bash -c "docker rm dev || true"'
        return cmd

    def build():
        cmd = f'bash -c "docker run --name dev -i -p 127.0.0.1:9087:9087 -p 127.0.0.1:8085:8085 -p 127.0.0.1:8761:8761 -v .:/home/dev -t  development:latest /bin/bash"'
        return cmd
    return {
        'actions': [
            Interactive(cleanup),
            Interactive(build),
            Interactive(success)],
    }


def task_attach_container():
    """Attach docker container if it is exists (after doit container)"""
    def build():
        cmd = f'bash -c "docker exec -it dev /bin/bash"'
        return cmd
    return {
        'actions': [Interactive(build), Interactive(success)],
    }


def task_clean_images():
    """Remove docker images"""
    def kill() -> str:
        cmd = f'bash -c "docker container kill $(docker ps -q) || true"'
        return cmd

    def remove() -> str:
        cmd = f'bash -c "docker image rm -f $(docker image ls -q) || true"'
        return cmd

    return {
        'actions': [
            Interactive(kill),
            Interactive(remove),
            Interactive(success)]}


def task_ms_package():
    """Build messenger jar-archive with the crc-checking for maven packages"""
    def package(tests: str):
        cmd = f'bash -c "cd messenger && mvnd -C package -Dmaven.test.skip={tests}"'
        return cmd
    return {
        'actions': [Interactive(package), Interactive(success)],
        'params': [
            tests_arg()],
    }


def task_gateway_package():
    """Build api-gateway jar-archive with the crc-checking for maven packages"""
    def package(tests: str):
        cmd = f'bash -c "cd services/api-gateway && mvnd -C package -Dmaven.test.skip={tests}"'
        return cmd
    return {
        'actions': [Interactive(package), Interactive(success)],
        'params': [
            tests_arg()],
    }


def task_eureka_package():
    """Build eureka jar-archive with the crc-checking for maven packages"""
    def package(tests: str):
        cmd = f'bash -c "cd services/eureka && mvnd -C package -Dmaven.test.skip={tests}"'
        return cmd
    return {
        'actions': [Interactive(package), Interactive(success)],
        'params': [
            tests_arg()],
    }


def task_package():
    """Build all packages with the crc-checking for maven packages"""
    def package_ms(tests: str):
        cmd = f'bash -c "cd messenger && mvnd -C package -Dmaven.test.skip={tests}"'
        return cmd

    def package_eureka(tests: str):
        cmd = f'bash -c "cd services/eureka && mvnd -C package -Dmaven.test.skip={tests}"'
        return cmd

    def package_gateway(tests: str):
        cmd = f'bash -c "cd services/api-gateway && mvnd -C package -Dmaven.test.skip={tests}"'
        return cmd
    return {
        'actions': [
            Interactive(package_ms),
            Interactive(package_eureka),
            Interactive(package_gateway),
            Interactive(success)],
        'params': [
            tests_arg()],
    }


def task_rm():
    """Remove all maven artifacts and out files"""

    def mvn_clean_out(version: str) -> str:
        cmd = f'bash -c "rm -rf ./out"'
        return cmd

    def mvn_clean_ms(version: str) -> str:
        cmd = f'bash -c "cd messenger && mvnd clean"'
        return cmd

    def mvn_clean_eureka(version: str) -> str:
        cmd = f'bash -c "cd services/eureka && mvnd clean"'
        return cmd

    def mvn_clean_gateway(version: str) -> str:
        cmd = f'bash -c "cd services/api-gateway && mvnd clean"'
        return cmd

    return {
        'actions': [
            Interactive(mvn_clean_out),
            Interactive(mvn_clean_ms),
            Interactive(mvn_clean_eureka),
            Interactive(mvn_clean_gateway),
            Interactive(success)],
        'params': [
            version_arg()],
    }


def task_pg_start():
    """Start postgres cluster (Only for container!!!)"""

    def start():
        cmd = f"pg_ctlcluster 17 messenger start"
        return cmd
    return {
        'actions': [Interactive(start), Interactive(success)],
    }


def task_pg_stop():
    """Stop postgres cluster (Only for container!!!)"""

    def stop():
        cmd = f"pg_ctlcluster 17 messenger stop"
        return cmd
    return {
        'actions': [Interactive(stop), Interactive(success)],
    }


def task_pg_reload():
    """Reload postgres cluster (Only for container!!!)"""

    def reload():
        cmd = f"pg_ctlcluster 17 messenger reload"
        return cmd
    return {
        'actions': [Interactive(reload), Interactive(success)],
    }


def task_pg_restore():
    """Clear and fill postgres cluster (Only for container!!!)"""

    def fill():
        cmd = f"psql -U postgres -p 5435 -f messenger/src/main/resources/schema.sql"
        return cmd
    return {
        'actions': [Interactive(fill), Interactive(success)],
    }


def task_kafka_run():
    """Run kafka server (Only for container!!!)"""

    def run(mode: str) -> str:
        cmd = ""
        if (mode == "back"):
            cmd += "export KAFKA_CLUSTER_ID=$(/kafka_2.13-4.0.0/bin/kafka-storage.sh random-uuid)"
            cmd += " && /kafka_2.13-4.0.0/bin/kafka-storage.sh format --standalone -t $KAFKA_CLUSTER_ID -c /kafka_2.13-4.0.0/config/server.properties"
            cmd += f" && /kafka_2.13-4.0.0/bin/kafka-server-start.sh -daemon /kafka_2.13-4.0.0/config/server.properties"
        else:
            cmd += "export KAFKA_CLUSTER_ID=$(/kafka_2.13-4.0.0/bin/kafka-storage.sh random-uuid)"
            cmd += " && /kafka_2.13-4.0.0/bin/kafka-storage.sh format --standalone -t $KAFKA_CLUSTER_ID -c /kafka_2.13-4.0.0/config/server.properties"
            cmd += f" && /kafka_2.13-4.0.0/bin/kafka-server-start.sh /kafka_2.13-4.0.0/config/server.properties"
        return cmd

    return {
        'actions': [Interactive(run), Interactive(success)],
        'params': [mode_arg("back")],
    }


def task_ms_run():
    """Run messenger"""

    def run(mode: str, ssl: str, port: int) -> str:
        cmd = ""
        proto_http = "http"
        proto_ws = "ws"

        if (ssl == "true"):
            proto_http = "https"
            proto_ws = "wss"

        if (mode == "back"):
            cmd = f'cd out && java -server -jar messenger-0.0.1-spring-boot.jar --server.port={port} --server.ssl.enabled={ssl} --messenger.multi-instance=true --messenger.public-url="{proto_http}://localhost:{port}" --messenger.websocket.url="{proto_ws}://localhost:{port}/ws" &'
        else:
            cmd = f'cd out && java -server -jar messenger-0.0.1-spring-boot.jar --server.port={port} --server.ssl.enabled={ssl} --messenger.multi-instance=true --messenger.public-url="{proto_http}://localhost:{port}" --messenger.websocket.url="{proto_ws}://localhost:{port}/ws"'
        return cmd

    return {
        'actions': [Interactive(run), Interactive(success)],
        'params': [mode_arg(), ssl_arg(), port_arg()],
    }


def task_gt_run():
    """Run api-gateway"""

    def run(mode: str, ssl: str, port: int) -> str:
        cmd = ""
        proto_http = "http"
        proto_ws = "ws"

        if (ssl == "true"):
            proto_http = "https"
            proto_ws = "wss"

        if (mode == "back"):
            cmd = f'cd out && java -server -jar api-gateway-0.0.1-spring-boot.jar &'
        else:
            cmd = f'cd out && java -server -jar api-gateway-0.0.1-spring-boot.jar'
        return cmd

    return {
        'actions': [Interactive(run), Interactive(success)],
        'params': [mode_arg(), ssl_arg(), port_arg()],
    }


def task_sd_run():
    """Run eureka server"""

    def run(mode: str, ssl: str, port: int) -> str:
        cmd = ""
        proto_http = "http"
        proto_ws = "ws"

        if (ssl == "true"):
            proto_http = "https"
            proto_ws = "wss"

        if (mode == "back"):
            cmd = f'cd out && java -server -jar eureka-0.0.1-spring-boot.jar &'
        else:
            cmd = f'cd out && java -server -jar eureka-0.0.1-spring-boot.jar'
        return cmd

    return {
        'actions': [Interactive(run), Interactive(success)],
        'params': [mode_arg(), ssl_arg(), port_arg()],
    }


def task_codestyle():
    """Fix codestyle"""

    def fill():
        cmd = f"autopep8 --in-place --aggressive --aggressive dodo.py"
        return cmd
    return {
        'actions': [Interactive(fill), Interactive(success)],
    }


def task_all_run():
    """Run Eureka, API Gateway and Messenger"""

    def sd_run(ssl: str, port: int) -> str:
        cmd = ""
        proto_http = "http"
        proto_ws = "ws"

        if (ssl == "true"):
            proto_http = "https"
            proto_ws = "wss"

        cmd = f'cd out && java -server -jar eureka-0.0.1-spring-boot.jar &'
        return cmd

    def gt_run(ssl: str, port: int) -> str:
        cmd = ""
        proto_http = "http"
        proto_ws = "ws"

        if (ssl == "true"):
            proto_http = "https"
            proto_ws = "wss"

        cmd = f'cd out && java -server -jar api-gateway-0.0.1-spring-boot.jar &'
        return cmd

    def ms_run(ssl: str, port: int) -> str:
        cmd = ""
        proto_http = "http"
        proto_ws = "ws"

        if (ssl == "true"):
            proto_http = "https"
            proto_ws = "wss"

        cmd = f'cd out && java -server -jar messenger-0.0.1-spring-boot.jar --server.port={port} --server.ssl.enabled={ssl} --messenger.multi-instance=true --messenger.public-url="{proto_http}://localhost:{port}" --messenger.websocket.url="{proto_ws}://localhost:{port}/ws" &'

        return cmd

    return {
        'actions': [
            Interactive(sd_run),
            Interactive(gt_run),
            Interactive(ms_run),
            Interactive(success)],
        'params': [
            ssl_arg(),
            port_arg()],
    }
