import os
import subprocess
import paramiko

MAJORITY = "majority"
W1 = "w1"
LOCAL = "local"
TRUE = "true"
FALSE = "false"

LASTEST = "/home/young/DisAlg/Causal-Consistency/MongoDB/mongodb/store/latest/"
HISTORY_EDN = LASTEST + "history.edn"
JEPSEN_LOG = LASTEST + "jepsen.log"
SERVER_BASE = "/home/ouyanghongrong/selected-data/"
HOSTNAME = "114.212.86.195"
PORT = 22
USER = "ouyanghongrong"


class NetWorkManager(object):

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
            limit, w, r, shard_count, self.nodes, self.get_with_nemesis(with_nemesis), self.password, concurrency, op_counts,
            write_proportion, read_proportion
        )

class SSHSender(object):

    def __init__(self, label='') -> None:
        super().__init__()
        self.ssh_key_path = '/home/young/.ssh/id_rsa'
        self.ssh_client = paramiko.SSHClient()
        self.label = label
        try:
            self.ssh_client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
            self.ssh_client.connect(hostname=HOSTNAME, port=PORT, username=USER, key_filename=self.ssh_key_path)
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
                    if title.__contains__("majority"):
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
    cmd = runner.gen_commands(w=MAJORITY, r=MAJORITY, with_nemesis=True, auto_time=False, time_limit=10000, op_counts=1000)
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
    sender = SSHSender("_20201221")
    
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

    network_manager.check_network()
    cmd = runner.gen_commands(w=MAJORITY, r=MAJORITY, with_nemesis=True, auto_time=False, time_limit=361, op_counts=1500)
    print(cmd)
    res = os.system(cmd)
    print(res)
    sender.send_lastest_result()