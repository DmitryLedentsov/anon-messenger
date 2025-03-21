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


def system_arg(default="linux"):
    return {
        "name": "system",
        "short": "s",
        "long": "system",
        "choices": (("windows", ""), ("linux", "")),
        "type": str,
        "default": default,
        "help": "Set operation system for the command executing",
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


def task_build_images():
    """Build docker container development:latest"""
    def build():
        cmd = f"docker build --pull --rm -f Dockerfile -t development:latest ."
        return cmd
    return {
        'actions': [Interactive(build)],
    }


def task_container():
    """Run docker container development:latest"""
    def build():
        cmd = f"docker run -i -p 127.0.0.1:8081:8081 -v .:/home/dev -t  development:latest /bin/bash && cd /dev/home"
        return cmd
    return {
        'actions': [Interactive(build)],
    }


def task_clean_images():
    """Build docker container development:lates"""
    def kill() -> str:
        cmd = f'bash -c "docker container kill $(docker ps -q) || true"'
        return cmd

    def remove() -> str:
        cmd = f'bash -c "docker image rm -f $(docker image ls -q) || true"'
        return cmd

    return {
        'actions': [Interactive(kill), Interactive(remove), Interactive(success)]
    }


def task_package():
    """Build jar-archive with the crc-checking for maven packages"""
    def package():
        cmd = f'bash -c "cd messenger && mvnd package"'
        return cmd
    return {
        'actions': [Interactive(package)],
    }


def task_rm():
    """Remove all maven artifacts"""

    def mvn_clean(version: str) -> str:
        cmd = f'bash -c "rm -f docker-files/messenger-{version}-spring-boot.jar && cd messenger && mvnd clean"'
        return cmd

    return {
        'actions': [Interactive(mvn_clean)],
        'params': [version_arg()],
    }


def task_pg_start():
    """Start postgres cluster"""

    def start():
        cmd = f"pg_ctlcluster 17 messenger start"
        return cmd
    return {
        'actions': [Interactive(start)],
    }


def task_pg_stop():
    """Stop postgres cluster"""

    def stop():
        cmd = f"pg_ctlcluster 17 messenger stop"
        return cmd
    return {
        'actions': [Interactive(stop)],
    }


def task_pg_reload():
    """Reload postgres cluster"""

    def reload():
        cmd = f"pg_ctlcluster 17 messenger reload"
        return cmd
    return {
        'actions': [Interactive(reload)],
    }


def task_pg_fill():
    """Fill postgres cluster"""

    def fill():
        cmd = f"psql -U postgres -p 5435 -f docker-files/infra/installation/postgresql/scripts/create.sql"
        return cmd
    return {
        'actions': [Interactive(fill)],
    }


def task_pg_clear():
    """Clear postgres cluster"""

    def clear():
        cmd = f"psql -U postgres -p 5435 -f docker-files/infra/installation/postgresql/scripts/drop.sql"
        return cmd
    return {
        'actions': [Interactive(clear)],
    }


def task_kafka_run():
    """Run kafka server"""

    def run(mode: str) -> str:
        cmd = ""
        if (mode == "back"):
            cmd += f"/kafka_2.13-4.0.0/bin/kafka-server-start.sh -daemon /kafka_2.13-4.0.0/config/server.properties"
        else:
            cmd += f"/kafka_2.13-4.0.0/bin/kafka-server-start.sh /kafka_2.13-4.0.0/config/server.properties"
        return cmd

    return {
        'actions': [Interactive(run)],
        'params': [mode_arg("back")],
    }


def task_ms_run():
    """Run messenger"""

    def run(mode: str) -> str:
        cmd = ""
        if (mode == "back"):
            cmd = f"cd docker-files && java -server -jar messenger-0.0.1-spring-boot.jar &"
        else:
            cmd = f"cd docker-files && java -server -jar messenger-0.0.1-spring-boot.jar"
        return cmd

    return {
        'actions': [Interactive(run)],
        'params': [mode_arg()],
    }
