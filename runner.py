import os
import subprocess
import paramiko
import traceback

MAJORITY = "majority"
W1 = "w1"
LOCAL = "local"
TRUE = "true"
FALSE = "false"
LINEARIZABLE = "linearizable"

LASTEST = "/home/young/DisAlg/Causal-Consistency/MongoDB/mongodb/store/latest/"
HISTORY_EDN = LASTEST + "history.edn"
JEPSEN_LOG = LASTEST + "jepsen.log"
SERVER_BASE = "/home/ouyanghongrong/selected-data/"
HOSTNAME = "114.212.86.195"
PORT = 22
USER = "ouyanghongrong"

SENDER_LABEL = "_20210126"

def local_execute(command):
    with subprocess.Popen(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                          encoding="utf-8") as process:
        stdout = process.stdout.readline()
        stderr = process.stderr.readline()
        return stdout, stderr

class NetWorkManager(object):
    """
    管理网络资源；每次运行前通过ping baidu.com的结果判断校园网是否断开。若断开，则运行自动连接校园网的脚本
    """
    def __init__(self) -> None:
        super().__init__()
        self.login = "/home/young/DisAlg/Causal-Consistency/dhTemp/njuLogin.py"

    def check_url(self, url="www.baidu.com"):
        valid = os.system("ping -c 1 -W 1 {}".format(url))
        if valid == 0:
            return True
        else:
            return False

    def nju_login(self):
        os.system("python3 {}".format(self.login))

    def check_network(self):
        while not self.check_url():
            self.nju_login()


class JepsenRunner(object):
    def __init__(self, nodes='/home/young/nodes', password='disalg.root!') -> None:
        super().__init__()
        self.nodes = nodes
        self.password = password
        self.cmd_template = "lein run test -t causal-register --time-limit {} -w {} -r {} --shard-count {} " \
                            "--nodes-file {} --with-nemesis {} --password {} --concurrency {} --operation-counts {} " \
                            "--write-proportion {} --read-proportion {} --leave-db-running "

    def get_time_limit(self, w, r, with_nemesis, op_counts):
        if w == MAJORITY and r == MAJORITY:
            if with_nemesis:
                # TODO: 有网络分区
                return int(120 + 0.4 * op_counts)
            else:
                # 无网络分区
                return int(120 + 0.1 * op_counts)

        elif w == W1 and r == LOCAL:
            if with_nemesis:
                # TODO: 有网络分区
                return int(120 + 0.4 * op_counts)
            else:
                # TODO: 无网络分区
                return int(120 + 0.4 * op_counts)
        elif w == MAJORITY and r == LINEARIZABLE:
            if with_nemesis:
                # TODO: 有网络分区
                return int(120 + 0.4 * op_counts)
            else:
                # TODO: 无网络分区
                return int(120 + 0.4 * op_counts)
        else:
            assert 0

    def get_with_nemesis(self, with_nemesis: bool):
        if with_nemesis:
            return "true"
        else:
            return "false"

    def gen_commands(self, time_limit=360, w=MAJORITY, r=MAJORITY, shard_count=2, with_nemesis=True, concurrency=10,
                     op_counts=1000, write_proportion=0.25, read_proportion=0.75, auto_time=False):
        '''
        :param time_limit: Jepsen 测试运行时间
        :param w: writeConcern
        :param r: readConcern
        :param shard_count: 分片数
        :param with_nemesis: 是否要在网络分区，节点失效等情况下执行 MongoDB 的 Jepsen 测试
        :param concurrency: 并发数，并发对 MongoDB 进行操作的 Client 数目
        :param op_counts: 执行的操作数
        :param write_proportion: 写操作比例
        :param read_proportion: 读操作比例
        :param auto_time: 是否自动根据参数调整执行时间
        :return:
        '''
        limit = time_limit
        if auto_time:
            limit = self.get_time_limit(w=w, r=r, with_nemesis=with_nemesis, op_counts=op_counts)
        return self.cmd_template.format(
            limit, w, r, shard_count, self.nodes, self.get_with_nemesis(with_nemesis), self.password, concurrency,
            op_counts,
            write_proportion, read_proportion
        )


class SSHSender(object):

    def __init__(self, label='') -> None:
        super().__init__()
        # self.private_key = paramiko.RSAKey.from_private_key_file('/home/young/.ssh/id_rsa')
        self.ssh_client = paramiko.SSHClient()
        self.label = label
        try:
            self.ssh_client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
            self.ssh_client.connect(hostname=HOSTNAME, port=PORT, username=USER, password=USER)
            stdin, stdout, stderr = self.ssh_client.exec_command('echo "ok"')
        except paramiko.SSHException as e:
            print(e)

    def execute(self, command):
        stdin, stdout, stderr = self.ssh_client.exec_command(command=command)
        stdout = stdout.read().decode()
        stderr = stderr.read().decode()
        print("Executing {} in host {}@{}".format(command, USER, HOSTNAME))
        print("stdout is {}".format(stdout))
        print("stderr is {}".format(stderr))
        return stdout, stderr

    def get_local_md5sum(self, local):
        md5sum, stderr = local_execute("md5sum {}".format(local))
        print("local file's md5sum is {}".format(md5sum))
        if stderr == '':
            return md5sum.split()[0]


    def get_remote_md5sum(self, remote):
        md5sum, stderr = self.execute("md5sum {}".format(remote))
        print("remote file's md5sum is {}".format(md5sum))
        if stderr == '':
            return md5sum.split()[0]

    def check_file_exist(self, numbers, local, remote):
        print("local file is ", local)
        print("remote file is ", remote)
        if numbers == 0:
            return False
        md5sum_local = self.get_local_md5sum(local=local)
        md5sum_remote = self.get_remote_md5sum(remote=remote)
        return md5sum_local == md5sum_remote \
               and md5sum_remote is not None \
               and md5sum_local is not None

    def send_lastest_result(self):
        if os.path.exists(JEPSEN_LOG):
            command = "cat {} | grep mongo-causal-register | grep -v GMT".format(JEPSEN_LOG)
            with subprocess.Popen(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                                  encoding="utf-8") as process:
                stdout = process.stdout.readline()
                if stdout.__contains__("mongo-causal"):
                    title = stdout.strip(', \n"').strip('\n')
                    idx_ops = title.find("ops")
                    idx_no = title.find("no")
                    ops = title[idx_ops + 4:idx_no - 1]
                    if title.__contains__("linearizable"):
                        if title.__contains__("failure"):
                            dir = SERVER_BASE + "linearizable_failure/{}".format(title) + self.label
                        else:
                            dir = SERVER_BASE + "linearizable_stable/{}".format(title) + self.label
                    elif title.__contains__("majority"):
                        if title.__contains__("failure"):
                            dir = SERVER_BASE + "majority_failure/{}".format(title) + self.label
                        else:
                            dir = SERVER_BASE + "majority_stable/{}".format(title) + self.label
                    else:
                        if title.__contains__("failure"):
                            dir = SERVER_BASE + "local_failure/{}".format(title) + self.label
                        else:
                            dir = SERVER_BASE + "local_stable/{}".format(title) + self.label
                else:
                    return
        else:
            return

        if os.path.exists(HISTORY_EDN):
            stdout, stderr = self.execute("cat {}".format(dir))
            if stdout == '' and stderr != '':
                print("{} is not exist, create one".format(dir))
                stdout, stderr = self.execute("mkdir {}".format(dir))
            else:
                print(stdout)

            stdout, stderr = self.execute("find {} -name *.edn | wc -l".format(dir))
            stdout = stdout.strip('\n')
            try:
                if stdout != '':
                    # 加入一个关于重复上传文件的判断（同一份latest的文件只传一次）
                    numbers = int(stdout)

                    local_history = HISTORY_EDN
                    local_log = JEPSEN_LOG
                    pre_history = "{}/history{}_{}.edn".format(dir, ops, numbers-1)
                    pre_log = "{}/jepsen{}_{}.log".format(dir, ops, numbers-1)

                    history_flag = self.check_file_exist(numbers=numbers, local=local_history, remote=pre_history)
                    log_flag = self.check_file_exist(numbers=numbers, local=local_log, remote=pre_log)

                    if history_flag or log_flag:
                        return

                    history = "history{}_{}.edn".format(ops, stdout)
                    command1 = "scp {} {}@{}:{}/{}".format(HISTORY_EDN, USER, HOSTNAME, dir, history)
                    jepsen = "jepsen{}_{}.log".format(ops, stdout)
                    command2 = "scp {} {}@{}:{}/{}".format(JEPSEN_LOG, USER, HOSTNAME, dir, jepsen)

                    commands = [command1, command2]
                    for cmd in commands:
                        with subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                                              encoding="utf-8") as process:
                            print("executing {}".format(command))
                            print("stdout is {}".format(process.stdout.readline()))
                            print("stderr is {}".format(process.stderr.readline()))

            except Exception as e:
                print(e)

            return stdout, stderr, dir


def test_cmd():
    runner = JepsenRunner()
    cmd = runner.gen_commands(w=MAJORITY, r=MAJORITY, with_nemesis=False, auto_time=True, op_counts=1000)
    print(cmd)
    cmd = runner.gen_commands(w=MAJORITY, r=MAJORITY, with_nemesis=True, auto_time=True, op_counts=1000)
    print(cmd)
    cmd = runner.gen_commands(w=MAJORITY, r=MAJORITY, with_nemesis=True, auto_time=False, time_limit=10000,
                              op_counts=1000)
    print(cmd)


def test_network():
    nm = NetWorkManager()
    nm.check_network()


if __name__ == '__main__':
    # WriteConcern and ReadConcern
    concern_pair = []
    concern_pair.append({'w': 'w1', 'r': 'local'})
    concern_pair.append({'w': 'majority', 'r': 'majority'})

    # with-nemesis majority majority CC

    # with-nemesis w1 local Not CC

    # without-nemesis w1 local CC

    runner = JepsenRunner()
    network_manager = NetWorkManager()

    #  for i in range(2000, 5001, 500):for
    #     for with_nemesis in [True, False]:
    #         for wr in [(MAJORITY, MAJORITY), (W1, LOCAL)]:
    #             network_manager.check_network()
    #             cmd = runner.gen_commands(w=wr[0], r=wr[1], with_nemesis=with_nemesis, auto_time=True, op_counts=int(i))
    #             print(cmd)
    #             res = os.system(cmd)
    #             print(res)
    #             try:
    #                 sender = SSHSender("_20201222")
    #                 sender.send_lastest_result()
    #             except Exception as e:
    #                 traceback.print_exc()
    # for i in range(0, 2001, 100):
    #     for with_nemesis in [True, False]:
    #         for wr in [(MAJORITY, MAJORITY), (W1, LOCAL), (MAJORITY, LINEARIZABLE)]:
    #             network_manager.check_network()
    #             cmd = runner.gen_commands(w=wr[0], r=wr[1], with_nemesis=with_nemesis, auto_time=True, op_counts=int(i))
    #             print(cmd)
    #             res = os.system(cmd)
    #             print(res)
    #             try:
    #                 sender = SSHSender("_20210116")
    #                 sender.send_lastest_result()
    #             except Exception as e:
    #                 traceback.print_exc()

    for j in range(0, 10, 1):
        for i in range(0, 1001, 100):
            for with_nemesis in [True, False]:
                for wr in [(MAJORITY, MAJORITY), (W1, LOCAL)]:
                    network_manager.check_network()
                    cmd = runner.gen_commands(w=wr[0], r=wr[1], with_nemesis=with_nemesis, auto_time=True, op_counts=int(i))
                    print(cmd)
                    res = os.system(cmd)
                    print(res)
                    try:
                        sender = SSHSender(SENDER_LABEL)
                        sender.send_lastest_result()
                        print("send",j,i,with_nemesis,wr)
                    except Exception as e:
                        traceback.print_exc()

    # for i in range(2000, 5000, 500):
    #     for with_nemesis in [True, False]:
    #         for wr in [(MAJORITY, MAJORITY), (W1, LOCAL), (MAJORITY, LINEARIZABLE)]:
    #             network_manager.check_network()
    #             cmd = runner.gen_commands(w=wr[0], r=wr[1], with_nemesis=with_nemesis, auto_time=True, op_counts=int(i))
    #             print(cmd)
    #             res = os.system(cmd)
    #             print(res)
    #             try:
    #                 sender = SSHSender("_20210116")
    #                 sender.send_lastest_result()
    #             except Exception as e:
    #                 traceback.print_exc()

    # for i in range(2000, 5001, 100):
    #     network_manager.check_network()
    #     cmd = runner.gen_commands(w="majority", r="majority", with_nemesis=False, auto_time=True, op_counts=int(i))
    #     print(cmd)
    #     res = os.system(cmd)
    #     print(res)
    #     sender.send_lastest_result()

    # for i in range(2000, 5001, 500):
    #     network_manager.check_network()
    #     cmd = runner.gen_commands(w="w1", r="local", with_nemesis=True, auto_time=True, op_counts=int(i))
    #     print(cmd)
    #     res = os.system(cmd)
    #     print(res)
    #     sender.send_lastest_result()

    # for i in range(2000, 5001, 500):
    #     network_manager.check_network()
    #     cmd = runner.gen_commands(w="majority", r="majority", with_nemesis=True, auto_time=True, op_counts=int(i))
    #     print(cmd)
    #     res = os.system(cmd)
    #     print(res)
    #     sender.send_lastest_result()

    # for i in range(2000, 5001, 500):
    #     network_manager.check_network()
    #     cmd = runner.gen_commands(w="w1", r="local", with_nemesis=False, auto_time=True, op_counts=int(i))
    #     print(cmd)
    #     res = os.system(cmd)
    #     print(res)
    #     sender.send_lastest_result()

