(ns jepsen.mongodb.adhoc-history
  (:require [clojure.test :refer :all]))

(def history [{:type :invoke, :f :read-init, :value nil, :process 0, :time 2287814518, :index 9}
              {:type :ok, :f :read-init, :value 0, :process 0, :time 2479949834, :position 6804624988948135939, :link :init, :index 11}
              {:type :invoke, :f :write, :value 1, :process 0, :time 4239153678, :index 30}
              {:type :ok, :f :write, :value 1, :process 0, :time 4281674778, :position 6804624997538070535, :link 6804624988948135939, :index 31}
              {:type :invoke, :f :read, :value nil, :process 0, :time 4536743105, :index 36}
              {:type :ok, :f :read, :value 1, :process 0, :time 4575867987, :position 6804624997538070536, :link 6804624997538070535, :index 38}
              {:type :invoke, :f :write, :value 2, :process 0, :time 6471269460, :index 58}
              {:type :ok, :f :write, :value 2, :process 0, :time 6509639814, :position 6804625006128005123, :link 6804624997538070536, :index 59}
              {:type :invoke, :f :read, :value nil, :process 0, :time 7707810436, :index 66}
              {:type :ok, :f :read, :value 2, :process 0, :time 7747829391, :position 6804625006128005123, :link 6804625006128005123, :index 67}
              {:type :invoke, :f :write, :value 3, :process 0, :time 8693011570, :index 74}
              {:type :ok, :f :write, :value 3, :process 0, :time 8734829046, :position 6804625014717939715, :link 6804625006128005123, :index 75}
              {:type :invoke, :f :read, :value nil, :process 0, :time 9936926886, :index 88}
              {:type :ok, :f :read, :value 3, :process 0, :time 9974366895, :position 6804625014717939717, :link 6804625014717939715, :index 89}
              {:type :info, :f :move, :process :nemesis, :time 10324597735, :index 96}
              {:type :info, :f :move, :process :nemesis, :time 10332888069, :error "indeterminate: ", :index 97}
              {:type :info, :f :move, :process :nemesis, :time 10833953436, :index 102}
              {:type :info, :f :move, :process :nemesis, :time 10836730146, :error "indeterminate: ", :index 103}
              {:type :invoke, :f :write, :value 4, :process 0, :time 11336552813, :index 104}
              {:type :info, :f :move, :process :nemesis, :time 11337399059, :index 105}
              {:type :info, :f :move, :process :nemesis, :time 11341288286, :error "indeterminate: ", :index 106}
              {:type :ok, :f :write, :value 4, :process 0, :time 11384631723, :position 6804625027602841601, :link 6804625014717939717, :index 107}
              {:type :invoke, :f :read, :value nil, :process 0, :time 11797697342, :index 112}
              {:type :ok, :f :read, :value 4, :process 0, :time 11832623947, :position 6804625027602841601, :link 6804625027602841601, :index 113}
              {:type :info, :f :start, :process :nemesis, :time 11842222956, :index 114}
              {:type :info, :f :start, :process :nemesis, :time 12393494086, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227" "175.24.76.99"}, "175.24.75.168" #{"212.64.58.158" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"212.64.58.158" "212.64.86.223"}}], :index 118}
              {:type :invoke, :f :write, :value 5, :process 0, :time 12635970877, :index 119}
              {:type :ok, :f :write, :value 5, :process 0, :time 12682823915, :position 6804625031897808897, :link 6804625027602841601, :index 121}
              {:type :invoke, :f :read, :value nil, :process 0, :time 13160114445, :index 124}
              {:type :ok, :f :read, :value 5, :process 0, :time 13199696604, :position 6804625031897808897, :link 6804625031897808897, :index 126}
              {:type :info, :f :stop, :process :nemesis, :time 32395628725, :index 236}
              {:type :info, :f :stop, :process :nemesis, :time 32665588465, :value :network-healed, :index 237}
              {:type :info, :f :move, :process :nemesis, :time 42666609079, :index 304}
              {:type :info, :f :move, :process :nemesis, :time 42669414485, :error "indeterminate: ", :index 305}
              {:type :info, :f :move, :process :nemesis, :time 43170067107, :index 308}
              {:type :info, :f :move, :process :nemesis, :time 43172656983, :error "indeterminate: ", :index 309}
              {:type :info, :f :move, :process :nemesis, :time 43673367894, :index 316}
              {:type :info, :f :move, :process :nemesis, :time 43677038034, :error "indeterminate: ", :index 317}
              {:type :info, :f :start, :process :nemesis, :time 44177791437, :index 322}
              {:type :info, :f :start, :process :nemesis, :time 44314766737, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.75.168" #{"175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.106.227" "175.24.76.99"}}], :index 324}
              {:type :info, :f :stop, :process :nemesis, :time 64316293559, :index 346}
              {:type :info, :f :stop, :process :nemesis, :time 64582742452, :value :network-healed, :index 347}
              {:type :info, :f :move, :process :nemesis, :time 74583763670, :index 404}
              {:type :info, :f :move, :process :nemesis, :time 74586912748, :error "indeterminate: ", :index 405}
              {:type :info, :f :move, :process :nemesis, :time 75087630639, :index 410}
              {:type :info, :f :move, :process :nemesis, :time 75090790960, :error "indeterminate: ", :index 411}
              {:type :info, :f :move, :process :nemesis, :time 75591650283, :index 414}
              {:type :info, :f :move, :process :nemesis, :time 75593928013, :error "indeterminate: ", :index 415}
              {:type :info, :f :start, :process :nemesis, :time 76094529433, :index 420}
              {:type :info, :f :start, :process :nemesis, :time 76238371764, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.75.168" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.86.223"}}], :index 421}
              {:type :info, :f :stop, :process :nemesis, :time 96239578153, :index 437}
              {:type :info, :f :stop, :process :nemesis, :time 96502895650, :value :network-healed, :index 438}
              {:type :info, :f :move, :process :nemesis, :time 106503896802, :index 466}
              {:type :info, :f :move, :process :nemesis, :time 106504836932, :error "indeterminate: ", :index 467}
              {:type :info, :f :move, :process :nemesis, :time 107005750400, :index 468}
              {:type :info, :f :move, :process :nemesis, :time 107008100928, :error "indeterminate: ", :index 469}
              {:type :info, :f :move, :process :nemesis, :time 107508745795, :index 472}
              {:type :info, :f :move, :process :nemesis, :time 107511521394, :error "indeterminate: ", :index 473}
              {:type :info, :f :start, :process :nemesis, :time 108012250743, :index 478}
              {:type :info, :f :start, :process :nemesis, :time 108259071011, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.75.168" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.86.223"}}], :index 483}
              {:type :info, :f :stop, :process :nemesis, :time 128259872560, :index 497}
              {:type :info, :f :stop, :process :nemesis, :time 130508662966, :value :network-healed, :index 498}
              {:type :info, :f :move, :process :nemesis, :time 140509543327, :index 538}
              {:type :info, :f :move, :process :nemesis, :time 140512670874, :error "indeterminate: ", :index 539}
              {:type :info, :f :move, :process :nemesis, :time 141013553639, :index 544}
              {:type :info, :f :move, :process :nemesis, :time 141016056582, :error "indeterminate: ", :index 545}
              {:type :info, :f :move, :process :nemesis, :time 141516759429, :index 550}
              {:type :info, :f :move, :process :nemesis, :time 141518753907, :error "indeterminate: ", :index 551}
              {:type :info, :f :start, :process :nemesis, :time 142019284224, :index 557}
              {:type :info, :f :start, :process :nemesis, :time 142165289395, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.75.168" #{"175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.106.227" "175.24.76.99"}}], :index 560}
              {:type :info, :f :stop, :process :nemesis, :time 162166892021, :index 594}
              {:type :info, :f :stop, :process :nemesis, :time 165183506729, :value :network-healed, :index 605}
              {:type :info, :f :move, :process :nemesis, :time 175184404412, :index 676}
              {:type :info, :f :move, :process :nemesis, :time 175186809179, :error "indeterminate: ", :index 677}
              {:type :info, :f :move, :process :nemesis, :time 175687414171, :index 682}
              {:type :info, :f :move, :process :nemesis, :time 175689249253, :error "indeterminate: ", :index 683}
              {:type :info, :f :move, :process :nemesis, :time 176189805892, :index 694}
              {:type :info, :f :move, :process :nemesis, :time 176192442345, :error "indeterminate: ", :index 695}
              {:type :info, :f :start, :process :nemesis, :time 176693197741, :index 702}
              {:type :info, :f :start, :process :nemesis, :time 176832875500, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "175.24.75.168" #{"175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.106.227" "212.64.86.223"}}], :index 703}
              {:type :info, :f :stop, :process :nemesis, :time 196834039133, :index 829}
              {:type :info, :f :stop, :process :nemesis, :time 197104116961, :value :network-healed, :index 835}
              {:type :info, :f :move, :process :nemesis, :time 207104726566, :index 896}
              {:type :info, :f :move, :process :nemesis, :time 207106541607, :error "indeterminate: ", :index 897}
              {:type :info, :f :move, :process :nemesis, :time 207607093920, :index 904}
              {:type :info, :f :move, :process :nemesis, :time 207609873990, :error "indeterminate: ", :index 905}
              {:type :info, :f :move, :process :nemesis, :time 208110649192, :index 910}
              {:type :info, :f :move, :process :nemesis, :time 208112753850, :error "indeterminate: ", :index 911}
              {:type :info, :f :start, :process :nemesis, :time 208613299117, :index 918}
              {:type :info, :f :start, :process :nemesis, :time 208747190317, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "175.24.75.168" #{"175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.106.227" "212.64.86.223"}}], :index 919}
              {:type :info, :f :stop, :process :nemesis, :time 228748411561, :index 1032}
              {:type :info, :f :stop, :process :nemesis, :time 229020559906, :value :network-healed, :index 1033}
              {:type :info, :f :move, :process :nemesis, :time 239021370448, :index 1090}
              {:type :info, :f :move, :process :nemesis, :time 239023614639, :error "indeterminate: ", :index 1091}
              {:type :info, :f :move, :process :nemesis, :time 239524285676, :index 1097}
              {:type :info, :f :move, :process :nemesis, :time 239526356871, :error "indeterminate: ", :index 1099}
              {:type :info, :f :move, :process :nemesis, :time 240026970853, :index 1106}
              {:type :info, :f :move, :process :nemesis, :time 240029426521, :error "indeterminate: ", :index 1107}
              {:type :info, :f :start, :process :nemesis, :time 240530042836, :index 1117}
              {:type :info, :f :start, :process :nemesis, :time 240671822230, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"212.64.58.158" "175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.76.99"}, "175.24.106.227" #{"175.24.75.168" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "175.24.76.99"}}], :index 1120}
              {:type :info, :f :stop, :process :nemesis, :time 260672956149, :index 1133}
              {:type :info, :f :stop, :process :nemesis, :time 260949831505, :value :network-healed, :index 1134}
              {:type :info, :f :move, :process :nemesis, :time 270950594630, :index 1161}
              {:type :info, :f :move, :process :nemesis, :time 270953215714, :error "indeterminate: ", :index 1162}
              {:type :info, :f :move, :process :nemesis, :time 271453731715, :index 1167}
              {:type :info, :f :move, :process :nemesis, :time 271457417429, :error "indeterminate: ", :index 1168}
              {:type :info, :f :move, :process :nemesis, :time 271957829438, :index 1174}
              {:type :info, :f :move, :process :nemesis, :time 271959668262, :error "indeterminate: ", :index 1175}
              {:type :info, :f :start, :process :nemesis, :time 272460070369, :index 1179}
              {:type :info, :f :start, :process :nemesis, :time 272600921101, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"212.64.58.158" "175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.76.99"}, "175.24.106.227" #{"175.24.75.168" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "175.24.76.99"}}], :index 1180}
              {:type :info, :f :stop, :process :nemesis, :time 292602055210, :index 1204}
              {:type :info, :f :stop, :process :nemesis, :time 292874945858, :value :network-healed, :index 1205}
              {:type :info, :f :move, :process :nemesis, :time 302875612392, :index 1257}
              {:type :info, :f :move, :process :nemesis, :time 302877241197, :error "indeterminate: ", :index 1258}
              {:type :info, :f :move, :process :nemesis, :time 303377708090, :index 1266}
              {:type :info, :f :move, :process :nemesis, :time 303379416494, :error "indeterminate: ", :index 1267}
              {:type :info, :f :move, :process :nemesis, :time 303879997339, :index 1280}
              {:type :info, :f :move, :process :nemesis, :time 303882049122, :error "indeterminate: ", :index 1281}
              {:type :info, :f :start, :process :nemesis, :time 304382560243, :index 1287}
              {:type :info, :f :start, :process :nemesis, :time 304522429765, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.106.227"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227"}}], :index 1292}
              {:type :info, :f :stop, :process :nemesis, :time 324523862777, :index 1307}
              {:type :info, :f :stop, :process :nemesis, :time 324836337522, :value :network-healed, :index 1308}
              {:type :info, :f :move, :process :nemesis, :time 334837055382, :index 1334}
              {:type :info, :f :move, :process :nemesis, :time 334839398297, :error "indeterminate: ", :index 1335}
              {:type :info, :f :move, :process :nemesis, :time 335340090028, :index 1342}
              {:type :info, :f :move, :process :nemesis, :time 335341881322, :error "indeterminate: ", :index 1343}
              {:type :info, :f :move, :process :nemesis, :time 335842307381, :index 1348}
              {:type :info, :f :move, :process :nemesis, :time 335843900140, :error "indeterminate: ", :index 1349}
              {:type :info, :f :start, :process :nemesis, :time 336344407395, :index 1352}
              {:type :info, :f :start, :process :nemesis, :time 336481567622, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.75.168" #{"175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.106.227" "175.24.76.99"}}], :index 1353}
              {:type :info, :f :stop, :process :nemesis, :time 356482874627, :index 1381}
              {:type :info, :f :stop, :process :nemesis, :time 356751275531, :value :network-healed, :index 1382}
              {:type :info, :f :move, :process :nemesis, :time 366751987347, :index 1441}
              {:type :info, :f :move, :process :nemesis, :time 366754207245, :error "indeterminate: ", :index 1443}
              {:type :info, :f :move, :process :nemesis, :time 367254783471, :index 1450}
              {:type :info, :f :move, :process :nemesis, :time 367256573017, :error "indeterminate: ", :index 1451}
              {:type :info, :f :move, :process :nemesis, :time 367757088872, :index 1456}
              {:type :info, :f :move, :process :nemesis, :time 367759036784, :error "indeterminate: ", :index 1457}
              {:type :info, :f :start, :process :nemesis, :time 368259606038, :index 1460}
              {:type :info, :f :start, :process :nemesis, :time 368398889335, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.106.227"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227"}}], :index 1463}
              {:type :info, :f :stop, :process :nemesis, :time 388399953571, :index 1475}
              {:type :info, :f :stop, :process :nemesis, :time 388667862057, :value :network-healed, :index 1476}
              {:type :info, :f :move, :process :nemesis, :time 398668574275, :index 1498}
              {:type :info, :f :move, :process :nemesis, :time 398670625533, :error "indeterminate: ", :index 1499}
              {:type :info, :f :move, :process :nemesis, :time 399171201766, :index 1504}
              {:type :info, :f :move, :process :nemesis, :time 399171893183, :error "indeterminate: ", :index 1505}
              {:type :info, :f :move, :process :nemesis, :time 399672253161, :index 1510}
              {:type :info, :f :move, :process :nemesis, :time 399673883481, :error "indeterminate: ", :index 1511}
              {:type :info, :f :start, :process :nemesis, :time 400174419182, :index 1517}
              {:type :info, :f :start, :process :nemesis, :time 400308531929, :value [:isolated {"175.24.76.99" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "175.24.75.168" #{"175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.76.99" "212.64.86.223"}}], :index 1519}
              {:type :info, :f :stop, :process :nemesis, :time 420309624148, :index 1531}
              {:type :info, :f :stop, :process :nemesis, :time 420575321080, :value :network-healed, :index 1532}
              {:type :info, :f :move, :process :nemesis, :time 430575770259, :index 1566}
              {:type :info, :f :move, :process :nemesis, :time 430577504422, :error "indeterminate: ", :index 1567}
              {:type :info, :f :move, :process :nemesis, :time 431077885759, :index 1573}
              {:type :info, :f :move, :process :nemesis, :time 431079023872, :error "indeterminate: ", :index 1574}
              {:type :info, :f :move, :process :nemesis, :time 431579424289, :index 1581}
              {:type :info, :f :move, :process :nemesis, :time 431580973279, :error "indeterminate: ", :index 1582}
              {:type :info, :f :start, :process :nemesis, :time 432081472446, :index 1586}
              {:type :info, :f :start, :process :nemesis, :time 432230961002, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "175.24.75.168" #{"175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.106.227" "212.64.86.223"}}], :index 1589}
              {:type :info, :f :stop, :process :nemesis, :time 452232051900, :index 1726}
              {:type :info, :f :stop, :process :nemesis, :time 452499398848, :value :network-healed, :index 1727}
              {:type :info, :f :move, :process :nemesis, :time 462500044438, :index 1800}
              {:type :info, :f :move, :process :nemesis, :time 462501765607, :error "indeterminate: ", :index 1801}
              {:type :info, :f :move, :process :nemesis, :time 463002204469, :index 1804}
              {:type :info, :f :move, :process :nemesis, :time 463004046650, :error "indeterminate: ", :index 1805}
              {:type :info, :f :move, :process :nemesis, :time 463504600217, :index 1810}
              {:type :info, :f :move, :process :nemesis, :time 463506445080, :error "indeterminate: ", :index 1811}
              {:type :info, :f :start, :process :nemesis, :time 464006944067, :index 1816}
              {:type :info, :f :start, :process :nemesis, :time 464145374588, :value [:isolated {"175.24.75.168" #{"175.24.106.227" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.58.158"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158"}}], :index 1818}
              {:type :info, :f :stop, :process :nemesis, :time 484146461005, :index 1831}
              {:type :info, :f :stop, :process :nemesis, :time 484412536295, :value :network-healed, :index 1832}
              {:type :info, :f :move, :process :nemesis, :time 494413168266, :index 1866}
              {:type :info, :f :move, :process :nemesis, :time 494415159995, :error "indeterminate: ", :index 1867}
              {:type :info, :f :move, :process :nemesis, :time 494915650466, :index 1876}
              {:type :info, :f :move, :process :nemesis, :time 494917967559, :error "indeterminate: ", :index 1877}
              {:type :info, :f :move, :process :nemesis, :time 495418632193, :index 1880}
              {:type :info, :f :move, :process :nemesis, :time 495420736237, :error "indeterminate: ", :index 1881}
              {:type :info, :f :start, :process :nemesis, :time 495921241714, :index 1888}
              {:type :info, :f :start, :process :nemesis, :time 496060420744, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "175.24.76.99" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.106.227"}, "175.24.76.99" #{"212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227"}}], :index 1889}
              {:type :info, :f :stop, :process :nemesis, :time 516060965931, :index 2020}
              {:type :info, :f :stop, :process :nemesis, :time 516342729115, :value :network-healed, :index 2023}
              {:type :info, :f :move, :process :nemesis, :time 526343368283, :index 2090}
              {:type :info, :f :move, :process :nemesis, :time 526345584067, :error "indeterminate: ", :index 2092}
              {:type :info, :f :move, :process :nemesis, :time 526846156771, :index 2098}
              {:type :info, :f :move, :process :nemesis, :time 526847697241, :error "indeterminate: ", :index 2099}
              {:type :info, :f :move, :process :nemesis, :time 527348123578, :index 2106}
              {:type :info, :f :move, :process :nemesis, :time 527350156252, :error "indeterminate: ", :index 2107}
              {:type :info, :f :start, :process :nemesis, :time 527850736115, :index 2108}
              {:type :info, :f :start, :process :nemesis, :time 527986012721, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.76.99"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.76.99"}}], :index 2109}
              {:type :info, :f :stop, :process :nemesis, :time 547986475115, :index 2136}
              {:type :info, :f :stop, :process :nemesis, :time 548254317026, :value :network-healed, :index 2137}
              {:type :info, :f :move, :process :nemesis, :time 558254945976, :index 2190}
              {:type :info, :f :move, :process :nemesis, :time 558256857245, :error "indeterminate: ", :index 2191}
              {:type :info, :f :move, :process :nemesis, :time 558757336092, :index 2198}
              {:type :info, :f :move, :process :nemesis, :time 558759036847, :error "indeterminate: ", :index 2199}
              {:type :info, :f :move, :process :nemesis, :time 559259496832, :index 2204}
              {:type :info, :f :move, :process :nemesis, :time 559261380244, :error "indeterminate: ", :index 2205}
              {:type :info, :f :start, :process :nemesis, :time 559761889076, :index 2208}
              {:type :info, :f :start, :process :nemesis, :time 559902060362, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.75.168" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.86.223"}}], :index 2209}
              {:type :info, :f :stop, :process :nemesis, :time 579903084868, :index 2225}
              {:type :info, :f :stop, :process :nemesis, :time 580167738867, :value :network-healed, :index 2226}
              {:type :info, :f :move, :process :nemesis, :time 590168547979, :index 2254}
              {:type :info, :f :move, :process :nemesis, :time 590170264504, :error "indeterminate: ", :index 2255}
              {:type :info, :f :move, :process :nemesis, :time 590670707133, :index 2259}
              {:type :info, :f :move, :process :nemesis, :time 590672492639, :error "indeterminate: ", :index 2260}
              {:type :info, :f :move, :process :nemesis, :time 591172944594, :index 2266}
              {:type :info, :f :move, :process :nemesis, :time 591175100573, :error "indeterminate: ", :index 2267}
              {:type :info, :f :start, :process :nemesis, :time 591675652996, :index 2271}
              {:type :info, :f :start, :process :nemesis, :time 591812023388, :value [:isolated {"175.24.75.168" #{"175.24.106.227" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.58.158"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158"}}], :index 2275}
              {:type :info, :f :stop, :process :nemesis, :time 611813321049, :index 2309}
              {:type :info, :f :stop, :process :nemesis, :time 612077950464, :value :network-healed, :index 2311}
              {:type :info, :f :move, :process :nemesis, :time 622078607559, :index 2354}
              {:type :info, :f :move, :process :nemesis, :time 622080568369, :error "indeterminate: ", :index 2355}
              {:type :info, :f :move, :process :nemesis, :time 622581016990, :index 2362}
              {:type :info, :f :move, :process :nemesis, :time 622583104981, :error "indeterminate: ", :index 2363}
              {:type :info, :f :move, :process :nemesis, :time 623083572423, :index 2368}
              {:type :info, :f :move, :process :nemesis, :time 623084196176, :error "indeterminate: ", :index 2369}
              {:type :info, :f :start, :process :nemesis, :time 623584447190, :index 2371}
              {:type :info, :f :start, :process :nemesis, :time 623723506162, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.106.227"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227"}}], :index 2375}
              {:type :info, :f :stop, :process :nemesis, :time 643724760484, :index 2391}
              {:type :info, :f :stop, :process :nemesis, :time 643991754823, :value :network-healed, :index 2392}
              {:type :info, :f :move, :process :nemesis, :time 653992408220, :index 2414}
              {:type :info, :f :move, :process :nemesis, :time 653994763565, :error "indeterminate: ", :index 2415}
              {:type :info, :f :move, :process :nemesis, :time 654495305947, :index 2420}
              {:type :info, :f :move, :process :nemesis, :time 654497572593, :error "indeterminate: ", :index 2421}
              {:type :info, :f :move, :process :nemesis, :time 654998071671, :index 2429}
              {:type :info, :f :move, :process :nemesis, :time 655000127901, :error "indeterminate: ", :index 2430}
              {:type :info, :f :start, :process :nemesis, :time 655500648294, :index 2439}
              {:type :info, :f :start, :process :nemesis, :time 655638598071, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "175.24.76.99" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.106.227"}, "175.24.76.99" #{"212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227"}}], :index 2441}
              {:type :info, :f :stop, :process :nemesis, :time 675639792909, :index 2469}
              {:type :info, :f :stop, :process :nemesis, :time 675913151051, :value :network-healed, :index 2471}
              {:type :info, :f :move, :process :nemesis, :time 685913795336, :index 2513}
              {:type :info, :f :move, :process :nemesis, :time 685915356790, :error "indeterminate: ", :index 2514}
              {:type :info, :f :move, :process :nemesis, :time 686415798459, :index 2516}
              {:type :info, :f :move, :process :nemesis, :time 686417453660, :error "indeterminate: ", :index 2517}
              {:type :info, :f :move, :process :nemesis, :time 686917913711, :index 2525}
              {:type :info, :f :move, :process :nemesis, :time 686919894216, :error "indeterminate: ", :index 2526}
              {:type :info, :f :start, :process :nemesis, :time 687420413569, :index 2534}
              {:type :info, :f :start, :process :nemesis, :time 687583168282, :value [:isolated {"175.24.76.99" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "175.24.75.168" #{"175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.76.99" "212.64.86.223"}}], :index 2535}
              {:type :info, :f :stop, :process :nemesis, :time 707584704864, :index 2570}
              {:type :info, :f :stop, :process :nemesis, :time 707850581571, :value :network-healed, :index 2571}
              {:type :info, :f :move, :process :nemesis, :time 717851221253, :index 2612}
              {:type :info, :f :move, :process :nemesis, :time 717852880493, :error "indeterminate: ", :index 2613}
              {:type :info, :f :move, :process :nemesis, :time 718353312238, :index 2614}
              {:type :info, :f :move, :process :nemesis, :time 718354954804, :error "indeterminate: ", :index 2615}
              {:type :info, :f :move, :process :nemesis, :time 718855440129, :index 2626}
              {:type :info, :f :move, :process :nemesis, :time 718857393418, :error "indeterminate: ", :index 2627}
              {:type :info, :f :start, :process :nemesis, :time 719357926150, :index 2632}
              {:type :info, :f :start, :process :nemesis, :time 719532352370, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.76.99"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.76.99"}}], :index 2633}
              {:type :info, :f :stop, :process :nemesis, :time 739533433587, :index 2656}
              {:type :info, :f :stop, :process :nemesis, :time 739804921350, :value :network-healed, :index 2661}
              {:type :info, :f :move, :process :nemesis, :time 749805744853, :index 2700}
              {:type :info, :f :move, :process :nemesis, :time 749809558474, :error "indeterminate: ", :index 2701}
              {:type :info, :f :move, :process :nemesis, :time 750310156246, :index 2704}
              {:type :info, :f :move, :process :nemesis, :time 750312067733, :error "indeterminate: ", :index 2705}
              {:type :info, :f :move, :process :nemesis, :time 750812798253, :index 2712}
              {:type :info, :f :move, :process :nemesis, :time 750814528419, :error "indeterminate: ", :index 2713}
              {:type :info, :f :start, :process :nemesis, :time 751315006381, :index 2718}
              {:type :info, :f :start, :process :nemesis, :time 751447263071, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.75.168" #{"175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.106.227" "175.24.76.99"}}], :index 2719}
              {:type :info, :f :stop, :process :nemesis, :time 771448355138, :index 2844}
              {:type :info, :f :stop, :process :nemesis, :time 771718527526, :value :network-healed, :index 2849}
              {:type :info, :f :move, :process :nemesis, :time 781719246297, :index 2920}
              {:type :info, :f :move, :process :nemesis, :time 781721241249, :error "indeterminate: ", :index 2921}
              {:type :info, :f :move, :process :nemesis, :time 782221751602, :index 2929}
              {:type :info, :f :move, :process :nemesis, :time 782223478692, :error "indeterminate: ", :index 2930}
              {:type :info, :f :move, :process :nemesis, :time 782723836872, :index 2936}
              {:type :info, :f :move, :process :nemesis, :time 782725537998, :error "indeterminate: ", :index 2937}
              {:type :info, :f :start, :process :nemesis, :time 783226005869, :index 2940}
              {:type :info, :f :start, :process :nemesis, :time 783365446534, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "175.24.75.168" #{"175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.106.227" "212.64.86.223"}}], :index 2943}
              {:type :info, :f :stop, :process :nemesis, :time 803366421621, :index 2974}
              {:type :info, :f :stop, :process :nemesis, :time 803639246605, :value :network-healed, :index 2979}
              {:type :info, :f :move, :process :nemesis, :time 813639839889, :index 3014}
              {:type :info, :f :move, :process :nemesis, :time 813641788757, :error "indeterminate: ", :index 3015}
              {:type :info, :f :move, :process :nemesis, :time 814142258552, :index 3023}
              {:type :info, :f :move, :process :nemesis, :time 814145096479, :error "indeterminate: ", :index 3024}
              {:type :info, :f :move, :process :nemesis, :time 814645785344, :index 3028}
              {:type :info, :f :move, :process :nemesis, :time 814647896509, :error "indeterminate: ", :index 3029}
              {:type :info, :f :start, :process :nemesis, :time 815148355470, :index 3037}
              {:type :info, :f :start, :process :nemesis, :time 815285873170, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227" "175.24.76.99"}, "175.24.75.168" #{"212.64.58.158" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"212.64.58.158" "212.64.86.223"}}], :index 3039}
              {:type :info, :f :stop, :process :nemesis, :time 835286925917, :index 3064}
              {:type :info, :f :stop, :process :nemesis, :time 835561370435, :value :network-healed, :index 3067}
              {:type :info, :f :move, :process :nemesis, :time 845561906942, :index 3110}
              {:type :info, :f :move, :process :nemesis, :time 845563703318, :error "indeterminate: ", :index 3111}
              {:type :info, :f :move, :process :nemesis, :time 846064164597, :index 3116}
              {:type :info, :f :move, :process :nemesis, :time 846065949541, :error "indeterminate: ", :index 3117}
              {:type :info, :f :move, :process :nemesis, :time 846566449564, :index 3120}
              {:type :info, :f :move, :process :nemesis, :time 846568085870, :error "indeterminate: ", :index 3121}
              {:type :info, :f :start, :process :nemesis, :time 847068620828, :index 3129}
              {:type :info, :f :start, :process :nemesis, :time 847206999127, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "175.24.76.99" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.106.227"}, "175.24.76.99" #{"212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227"}}], :index 3133}
              {:type :info, :f :stop, :process :nemesis, :time 867208067608, :index 3167}
              {:type :info, :f :stop, :process :nemesis, :time 870675567193, :value :network-healed, :index 3181}
              {:type :info, :f :move, :process :nemesis, :time 880676150018, :index 3229}
              {:type :info, :f :move, :process :nemesis, :time 880676750759, :error "indeterminate: ", :index 3230}
              {:type :info, :f :move, :process :nemesis, :time 881177015892, :index 3231}
              {:type :info, :f :move, :process :nemesis, :time 881178785537, :error "indeterminate: ", :index 3232}
              {:type :info, :f :move, :process :nemesis, :time 881679370623, :index 3233}
              {:type :info, :f :move, :process :nemesis, :time 881683592320, :error "indeterminate: ", :index 3234}
              {:type :info, :f :start, :process :nemesis, :time 882184288932, :index 3235}
              {:type :info, :f :start, :process :nemesis, :time 882320235654, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.106.227"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227"}}], :index 3236}
              {:type :info, :f :stop, :process :nemesis, :time 902320736983, :index 3308}
              {:type :info, :f :stop, :process :nemesis, :time 902593354314, :value :network-healed, :index 3311}
              {:type :info, :f :move, :process :nemesis, :time 912593805822, :index 3413}
              {:type :info, :f :move, :process :nemesis, :time 912595814331, :error "indeterminate: ", :index 3414}
              {:type :info, :f :move, :process :nemesis, :time 913096348651, :index 3422}
              {:type :info, :f :move, :process :nemesis, :time 913098164869, :error "indeterminate: ", :index 3423}
              {:type :info, :f :move, :process :nemesis, :time 913598626727, :index 3426}
              {:type :info, :f :move, :process :nemesis, :time 913601659714, :error "indeterminate: ", :index 3427}
              {:type :info, :f :start, :process :nemesis, :time 914102347658, :index 3435}
              {:type :info, :f :start, :process :nemesis, :time 914246376771, :value [:isolated {"175.24.75.168" #{"175.24.106.227" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.58.158"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158"}}], :index 3439}
              {:type :info, :f :stop, :process :nemesis, :time 934246947911, :index 3461}
              {:type :info, :f :stop, :process :nemesis, :time 934515212564, :value :network-healed, :index 3462}
              {:type :info, :f :move, :process :nemesis, :time 944515826422, :index 3476}
              {:type :info, :f :move, :process :nemesis, :time 944518039374, :error "indeterminate: ", :index 3477}
              {:type :info, :f :move, :process :nemesis, :time 945018423573, :index 3484}
              {:type :info, :f :move, :process :nemesis, :time 945019027345, :error "indeterminate: ", :index 3485}
              {:type :info, :f :move, :process :nemesis, :time 945519204671, :index 3490}
              {:type :info, :f :move, :process :nemesis, :time 945519878948, :error "indeterminate: ", :index 3491}
              {:type :info, :f :start, :process :nemesis, :time 946020119872, :index 3498}
              {:type :info, :f :start, :process :nemesis, :time 946156382713, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.75.168" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.86.223"}}], :index 3500}
              {:type :info, :f :stop, :process :nemesis, :time 966156770449, :index 3534}
              {:type :info, :f :stop, :process :nemesis, :time 966462529285, :value :network-healed, :index 3535}
              {:type :info, :f :move, :process :nemesis, :time 976462824676, :index 3567}
              {:type :info, :f :move, :process :nemesis, :time 976463605974, :error "indeterminate: ", :index 3568}
              {:type :info, :f :move, :process :nemesis, :time 976963893061, :index 3572}
              {:type :info, :f :move, :process :nemesis, :time 976964664942, :error "indeterminate: ", :index 3573}
              {:type :info, :f :move, :process :nemesis, :time 977464918223, :index 3579}
              {:type :info, :f :move, :process :nemesis, :time 977465693375, :error "indeterminate: ", :index 3580}
              {:type :info, :f :start, :process :nemesis, :time 977966408674, :index 3584}
              {:type :info, :f :start, :process :nemesis, :time 978143409667, :value [:isolated {"175.24.76.99" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "175.24.75.168" #{"175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.76.99" "212.64.86.223"}}], :index 3587}
              {:type :info, :f :stop, :process :nemesis, :time 998143932572, :index 3704}
              {:type :info, :f :stop, :process :nemesis, :time 998418356378, :value :network-healed, :index 3708}
              {:type :info, :f :move, :process :nemesis, :time 1008418887769, :index 3788}
              {:type :info, :f :move, :process :nemesis, :time 1008420355040, :error "indeterminate: ", :index 3789}
              {:type :info, :f :move, :process :nemesis, :time 1008920704723, :index 3797}
              {:type :info, :f :move, :process :nemesis, :time 1008922344492, :error "indeterminate: ", :index 3798}
              {:type :info, :f :move, :process :nemesis, :time 1009422790758, :index 3806}
              {:type :info, :f :move, :process :nemesis, :time 1009424508650, :error "indeterminate: ", :index 3807}
              {:type :info, :f :start, :process :nemesis, :time 1009924920195, :index 3808}
              {:type :info, :f :start, :process :nemesis, :time 1010065185885, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.75.168" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.86.223"}}], :index 3809}
              {:type :info, :f :stop, :process :nemesis, :time 1030066185268, :index 3823}
              {:type :info, :f :stop, :process :nemesis, :time 1030338951181, :value :network-healed, :index 3824}
              {:type :info, :f :move, :process :nemesis, :time 1040339472452, :index 3849}
              {:type :info, :f :move, :process :nemesis, :time 1040340953489, :error "indeterminate: ", :index 3850}
              {:type :info, :f :move, :process :nemesis, :time 1040841349473, :index 3856}
              {:type :info, :f :move, :process :nemesis, :time 1040842473726, :error "indeterminate: ", :index 3857}
              {:type :info, :f :move, :process :nemesis, :time 1041342861803, :index 3860}
              {:type :info, :f :move, :process :nemesis, :time 1041344171002, :error "indeterminate: ", :index 3861}
              {:type :info, :f :start, :process :nemesis, :time 1041844576023, :index 3868}
              {:type :info, :f :start, :process :nemesis, :time 1042026275981, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"212.64.58.158" "175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.76.99"}, "175.24.106.227" #{"175.24.75.168" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "175.24.76.99"}}], :index 3873}
              {:type :info, :f :stop, :process :nemesis, :time 1062027348317, :index 3891}
              {:type :info, :f :stop, :process :nemesis, :time 1064757411662, :value :network-healed, :index 3892}
              {:type :info, :f :move, :process :nemesis, :time 1074758015875, :index 3931}
              {:type :info, :f :move, :process :nemesis, :time 1074759286180, :error "indeterminate: ", :index 3932}
              {:type :info, :f :move, :process :nemesis, :time 1075259709025, :index 3940}
              {:type :info, :f :move, :process :nemesis, :time 1075261323332, :error "indeterminate: ", :index 3941}
              {:type :info, :f :move, :process :nemesis, :time 1075761690553, :index 3943}
              {:type :info, :f :move, :process :nemesis, :time 1075763245845, :error "indeterminate: ", :index 3944}
              {:type :info, :f :start, :process :nemesis, :time 1076263621997, :index 3952}
              {:type :info, :f :start, :process :nemesis, :time 1076402886160, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.75.168" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.86.223"}}], :index 3953}
              {:type :info, :f :stop, :process :nemesis, :time 1096404039239, :index 3973}
              {:type :info, :f :stop, :process :nemesis, :time 1096669483004, :value :network-healed, :index 3974}
              {:type :info, :f :move, :process :nemesis, :time 1106669877928, :index 4012}
              {:type :info, :f :move, :process :nemesis, :time 1106671293406, :error "indeterminate: ", :index 4013}
              {:type :info, :f :move, :process :nemesis, :time 1107171700416, :index 4018}
              {:type :info, :f :move, :process :nemesis, :time 1107173230148, :error "indeterminate: ", :index 4019}
              {:type :info, :f :move, :process :nemesis, :time 1107673638806, :index 4025}
              {:type :info, :f :move, :process :nemesis, :time 1107675588031, :error "indeterminate: ", :index 4026}
              {:type :info, :f :start, :process :nemesis, :time 1108176062490, :index 4030}
              {:type :info, :f :start, :process :nemesis, :time 1108320757488, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "175.24.75.168" #{"175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.106.227" "212.64.86.223"}}], :index 4031}
              {:type :info, :f :stop, :process :nemesis, :time 1128321712351, :index 4146}
              {:type :info, :f :stop, :process :nemesis, :time 1128597643556, :value :network-healed, :index 4151}
              {:type :info, :f :move, :process :nemesis, :time 1138598253390, :index 4222}
              {:type :info, :f :move, :process :nemesis, :time 1138599961362, :error "indeterminate: ", :index 4223}
              {:type :info, :f :move, :process :nemesis, :time 1139100367099, :index 4228}
              {:type :info, :f :move, :process :nemesis, :time 1139102136893, :error "indeterminate: ", :index 4229}
              {:type :info, :f :move, :process :nemesis, :time 1139602626932, :index 4242}
              {:type :info, :f :move, :process :nemesis, :time 1139604786107, :error "indeterminate: ", :index 4243}
              {:type :info, :f :start, :process :nemesis, :time 1140105275930, :index 4244}
              {:type :info, :f :start, :process :nemesis, :time 1140259230622, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "175.24.75.168" #{"175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.106.227" "212.64.86.223"}}], :index 4245}
              {:type :info, :f :stop, :process :nemesis, :time 1160260268814, :index 4380}
              {:type :info, :f :stop, :process :nemesis, :time 1160525159594, :value :network-healed, :index 4383}
              {:type :info, :f :move, :process :nemesis, :time 1170525747873, :index 4442}
              {:type :info, :f :move, :process :nemesis, :time 1170528160413, :error "indeterminate: ", :index 4443}
              {:type :info, :f :move, :process :nemesis, :time 1171028785173, :index 4448}
              {:type :info, :f :move, :process :nemesis, :time 1171030587818, :error "indeterminate: ", :index 4449}
              {:type :info, :f :move, :process :nemesis, :time 1171531067610, :index 4454}
              {:type :info, :f :move, :process :nemesis, :time 1171532888822, :error "indeterminate: ", :index 4455}
              {:type :info, :f :start, :process :nemesis, :time 1172033354517, :index 4461}
              {:type :info, :f :start, :process :nemesis, :time 1172169736464, :value [:isolated {"175.24.76.99" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "175.24.75.168" #{"175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.76.99" "212.64.86.223"}}], :index 4466}
              {:type :info, :f :stop, :process :nemesis, :time 1192170759388, :index 4580}
              {:type :info, :f :stop, :process :nemesis, :time 1192818874796, :value :network-healed, :index 4583}
              {:type :info, :f :move, :process :nemesis, :time 1202819424174, :index 4646}
              {:type :info, :f :move, :process :nemesis, :time 1202821102120, :error "indeterminate: ", :index 4647}
              {:type :info, :f :move, :process :nemesis, :time 1203321445932, :index 4654}
              {:type :info, :f :move, :process :nemesis, :time 1203322882042, :error "indeterminate: ", :index 4655}
              {:type :info, :f :move, :process :nemesis, :time 1203823290203, :index 4661}
              {:type :info, :f :move, :process :nemesis, :time 1203825331514, :error "indeterminate: ", :index 4662}
              {:type :info, :f :start, :process :nemesis, :time 1204325815950, :index 4669}
              {:type :info, :f :start, :process :nemesis, :time 1204595007391, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.106.227"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227"}}], :index 4671}
              {:type :info, :f :stop, :process :nemesis, :time 1224596007742, :index 4691}
              {:type :info, :f :stop, :process :nemesis, :time 1226738197109, :value :network-healed, :index 4692}
              {:type :info, :f :move, :process :nemesis, :time 1236738832236, :index 4725}
              {:type :info, :f :move, :process :nemesis, :time 1236740160231, :error "indeterminate: ", :index 4726}
              {:type :info, :f :move, :process :nemesis, :time 1237240606319, :index 4728}
              {:type :info, :f :move, :process :nemesis, :time 1237241875213, :error "indeterminate: ", :index 4729}
              {:type :info, :f :move, :process :nemesis, :time 1237742294211, :index 4733}
              {:type :info, :f :move, :process :nemesis, :time 1237743809831, :error "indeterminate: ", :index 4734}
              {:type :info, :f :start, :process :nemesis, :time 1238244214486, :index 4738}
              {:type :info, :f :start, :process :nemesis, :time 1238497646427, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.106.227"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227"}}], :index 4743}
              {:type :info, :f :stop, :process :nemesis, :time 1258498597462, :index 4757}
              {:type :info, :f :stop, :process :nemesis, :time 1261271717189, :value :network-healed, :index 4758}
              {:type :info, :f :move, :process :nemesis, :time 1271272073398, :index 4802}
              {:type :info, :f :move, :process :nemesis, :time 1271273506469, :error "indeterminate: ", :index 4803}
              {:type :info, :f :move, :process :nemesis, :time 1271773937689, :index 4813}
              {:type :info, :f :move, :process :nemesis, :time 1271775580124, :error "indeterminate: ", :index 4814}
              {:type :info, :f :move, :process :nemesis, :time 1272276029760, :index 4820}
              {:type :info, :f :move, :process :nemesis, :time 1272277460267, :error "indeterminate: ", :index 4821}
              {:type :info, :f :start, :process :nemesis, :time 1272777880538, :index 4828}
              {:type :info, :f :start, :process :nemesis, :time 1272924420903, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227" "175.24.76.99"}, "175.24.75.168" #{"212.64.58.158" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"212.64.58.158" "212.64.86.223"}}], :index 4829}
              {:type :info, :f :stop, :process :nemesis, :time 1292925427466, :index 4964}
              {:type :info, :f :stop, :process :nemesis, :time 1293241031846, :value :network-healed, :index 4967}
              {:type :info, :f :move, :process :nemesis, :time 1303241594927, :index 5038}
              {:type :info, :f :move, :process :nemesis, :time 1303243287281, :error "indeterminate: ", :index 5039}
              {:type :info, :f :move, :process :nemesis, :time 1303743729859, :index 5044}
              {:type :info, :f :move, :process :nemesis, :time 1303745236185, :error "indeterminate: ", :index 5045}
              {:type :info, :f :move, :process :nemesis, :time 1304245627835, :index 5052}
              {:type :info, :f :move, :process :nemesis, :time 1304247048852, :error "indeterminate: ", :index 5053}
              {:type :info, :f :start, :process :nemesis, :time 1304747377877, :index 5058}
              {:type :info, :f :start, :process :nemesis, :time 1304883194501, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "175.24.75.168" #{"175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.106.227" "212.64.86.223"}}], :index 5060}
              {:type :info, :f :stop, :process :nemesis, :time 1324883618964, :index 5086}
              {:type :info, :f :stop, :process :nemesis, :time 1325273109564, :value :network-healed, :index 5089}
              {:type :info, :f :move, :process :nemesis, :time 1335273715353, :index 5138}
              {:type :info, :f :move, :process :nemesis, :time 1335275346721, :error "indeterminate: ", :index 5139}
              {:type :info, :f :move, :process :nemesis, :time 1335775704066, :index 5146}
              {:type :info, :f :move, :process :nemesis, :time 1335777305400, :error "indeterminate: ", :index 5147}
              {:type :info, :f :move, :process :nemesis, :time 1336277757661, :index 5150}
              {:type :info, :f :move, :process :nemesis, :time 1336279463373, :error "indeterminate: ", :index 5151}
              {:type :info, :f :start, :process :nemesis, :time 1336779935682, :index 5156}
              {:type :info, :f :start, :process :nemesis, :time 1336937896474, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.76.99"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.76.99"}}], :index 5157}
              {:type :info, :f :stop, :process :nemesis, :time 1356938513816, :index 5183}
              {:type :info, :f :stop, :process :nemesis, :time 1357218791779, :value :network-healed, :index 5186}
              {:type :info, :f :move, :process :nemesis, :time 1367219408019, :index 5242}
              {:type :info, :f :move, :process :nemesis, :time 1367221203759, :error "indeterminate: ", :index 5243}
              {:type :info, :f :move, :process :nemesis, :time 1367721640084, :index 5247}
              {:type :info, :f :move, :process :nemesis, :time 1367723597893, :error "indeterminate: ", :index 5248}
              {:type :info, :f :move, :process :nemesis, :time 1368224061266, :index 5252}
              {:type :info, :f :move, :process :nemesis, :time 1368225752449, :error "indeterminate: ", :index 5253}
              {:type :info, :f :start, :process :nemesis, :time 1368726172057, :index 5261}
              {:type :info, :f :start, :process :nemesis, :time 1368870094564, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.75.168" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.86.223"}}], :index 5263}
              {:type :info, :f :stop, :process :nemesis, :time 1388870927087, :index 5277}
              {:type :info, :f :stop, :process :nemesis, :time 1389154374187, :value :network-healed, :index 5278}
              {:type :info, :f :move, :process :nemesis, :time 1399154977901, :index 5306}
              {:type :info, :f :move, :process :nemesis, :time 1399156799491, :error "indeterminate: ", :index 5307}
              {:type :info, :f :move, :process :nemesis, :time 1399657370296, :index 5313}
              {:type :info, :f :move, :process :nemesis, :time 1399658871277, :error "indeterminate: ", :index 5314}
              {:type :info, :f :move, :process :nemesis, :time 1400159274847, :index 5317}
              {:type :info, :f :move, :process :nemesis, :time 1400160723888, :error "indeterminate: ", :index 5318}
              {:type :info, :f :start, :process :nemesis, :time 1400661131933, :index 5323}
              {:type :info, :f :start, :process :nemesis, :time 1400811136890, :value [:isolated {"175.24.76.99" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "175.24.75.168" #{"175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.76.99" "212.64.86.223"}}], :index 5324}
              {:type :info, :f :stop, :process :nemesis, :time 1420812059316, :index 5329}
              {:type :info, :f :stop, :process :nemesis, :time 1421078114811, :value :network-healed, :index 5330}
              {:type :info, :f :move, :process :nemesis, :time 1431078476790, :index 5379}
              {:type :info, :f :move, :process :nemesis, :time 1431079830504, :error "indeterminate: ", :index 5380}
              {:type :info, :f :move, :process :nemesis, :time 1431580195886, :index 5388}
              {:type :info, :f :move, :process :nemesis, :time 1431580900884, :error "indeterminate: ", :index 5389}
              {:type :info, :f :move, :process :nemesis, :time 1432081202800, :index 5394}
              {:type :info, :f :move, :process :nemesis, :time 1432082561687, :error "indeterminate: ", :index 5395}
              {:type :info, :f :start, :process :nemesis, :time 1432582904936, :index 5396}
              {:type :info, :f :start, :process :nemesis, :time 1432719755616, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.75.168" #{"175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.106.227" "175.24.76.99"}}], :index 5397}
              {:type :info, :f :stop, :process :nemesis, :time 1452720615946, :index 5426}
              {:type :info, :f :stop, :process :nemesis, :time 1453003185442, :value :network-healed, :index 5428}
              {:type :info, :f :move, :process :nemesis, :time 1463003893326, :index 5470}
              {:type :info, :f :move, :process :nemesis, :time 1463005197020, :error "indeterminate: ", :index 5471}
              {:type :info, :f :move, :process :nemesis, :time 1463505608151, :index 5482}
              {:type :info, :f :move, :process :nemesis, :time 1463507295995, :error "indeterminate: ", :index 5483}
              {:type :info, :f :move, :process :nemesis, :time 1464007745065, :index 5484}
              {:type :info, :f :move, :process :nemesis, :time 1464009097521, :error "indeterminate: ", :index 5485}
              {:type :info, :f :start, :process :nemesis, :time 1464509499522, :index 5489}
              {:type :info, :f :start, :process :nemesis, :time 1464647275468, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "175.24.76.99" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.106.227"}, "175.24.76.99" #{"212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227"}}], :index 5491}
              {:type :info, :f :stop, :process :nemesis, :time 1484648448439, :index 5517}
              {:type :info, :f :stop, :process :nemesis, :time 1484916836695, :value :network-healed, :index 5520}
              {:type :info, :f :move, :process :nemesis, :time 1494917179352, :index 5577}
              {:type :info, :f :move, :process :nemesis, :time 1494918767744, :error "indeterminate: ", :index 5578}
              {:type :info, :f :move, :process :nemesis, :time 1495419191495, :index 5582}
              {:type :info, :f :move, :process :nemesis, :time 1495420729397, :error "indeterminate: ", :index 5583}
              {:type :info, :f :move, :process :nemesis, :time 1495921195714, :index 5585}
              {:type :info, :f :move, :process :nemesis, :time 1495922494009, :error "indeterminate: ", :index 5586}
              {:type :info, :f :start, :process :nemesis, :time 1496422880525, :index 5598}
              {:type :info, :f :start, :process :nemesis, :time 1496567929627, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.106.227"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227"}}], :index 5599}
              {:type :info, :f :stop, :process :nemesis, :time 1516569173183, :index 5615}
              {:type :info, :f :stop, :process :nemesis, :time 1516858565751, :value :network-healed, :index 5616}
              {:type :info, :f :move, :process :nemesis, :time 1526859097976, :index 5637}
              {:type :info, :f :move, :process :nemesis, :time 1526861087596, :error "indeterminate: ", :index 5638}
              {:type :info, :f :move, :process :nemesis, :time 1527361558699, :index 5645}
              {:type :info, :f :move, :process :nemesis, :time 1527362941535, :error "indeterminate: ", :index 5646}
              {:type :info, :f :move, :process :nemesis, :time 1527863278372, :index 5653}
              {:type :info, :f :move, :process :nemesis, :time 1527864654075, :error "indeterminate: ", :index 5654}
              {:type :info, :f :start, :process :nemesis, :time 1528364953011, :index 5658}
              {:type :info, :f :start, :process :nemesis, :time 1528500367759, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.75.168" #{"175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.106.227" "175.24.76.99"}}], :index 5659}
              {:type :info, :f :stop, :process :nemesis, :time 1548500759569, :index 5687}
              {:type :info, :f :stop, :process :nemesis, :time 1549181485626, :value :network-healed, :index 5689}
              {:type :info, :f :move, :process :nemesis, :time 1559181941434, :index 5748}
              {:type :info, :f :move, :process :nemesis, :time 1559182389025, :error "indeterminate: ", :index 5749}
              {:type :info, :f :move, :process :nemesis, :time 1559682653476, :index 5757}
              {:type :info, :f :move, :process :nemesis, :time 1559683917149, :error "indeterminate: ", :index 5758}
              {:type :info, :f :move, :process :nemesis, :time 1560184324548, :index 5764}
              {:type :info, :f :move, :process :nemesis, :time 1560185611343, :error "indeterminate: ", :index 5765}
              {:type :info, :f :start, :process :nemesis, :time 1560686013883, :index 5769}
              {:type :info, :f :start, :process :nemesis, :time 1560831439484, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"212.64.58.158" "175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.76.99"}, "175.24.106.227" #{"175.24.75.168" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "175.24.76.99"}}], :index 5771}
              {:type :info, :f :stop, :process :nemesis, :time 1580832291898, :index 5787}
              {:type :info, :f :stop, :process :nemesis, :time 1581097784919, :value :network-healed, :index 5788}
              {:type :info, :f :move, :process :nemesis, :time 1591098375653, :index 5808}
              {:type :info, :f :move, :process :nemesis, :time 1591100028542, :error "indeterminate: ", :index 5809}
              {:type :info, :f :move, :process :nemesis, :time 1591600487429, :index 5816}
              {:type :info, :f :move, :process :nemesis, :time 1591602161128, :error "indeterminate: ", :index 5817}
              {:type :info, :f :move, :process :nemesis, :time 1592102607366, :index 5825}
              {:type :info, :f :move, :process :nemesis, :time 1592104632632, :error "indeterminate: ", :index 5826}
              {:type :info, :f :start, :process :nemesis, :time 1592605133940, :index 5832}
              {:type :info, :f :start, :process :nemesis, :time 1592749037002, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.76.99"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.76.99"}}], :index 5836}
              {:type :info, :f :stop, :process :nemesis, :time 1612750226339, :index 5859}
              {:type :info, :f :stop, :process :nemesis, :time 1613020017029, :value :network-healed, :index 5860}
              {:type :info, :f :move, :process :nemesis, :time 1623020564261, :index 5907}
              {:type :info, :f :move, :process :nemesis, :time 1623022476143, :error "indeterminate: ", :index 5908}
              {:type :info, :f :move, :process :nemesis, :time 1623522932729, :index 5914}
              {:type :info, :f :move, :process :nemesis, :time 1623524582687, :error "indeterminate: ", :index 5915}
              {:type :info, :f :move, :process :nemesis, :time 1624025013641, :index 5920}
              {:type :info, :f :move, :process :nemesis, :time 1624026748235, :error "indeterminate: ", :index 5921}
              {:type :info, :f :start, :process :nemesis, :time 1624527247142, :index 5933}
              {:type :info, :f :start, :process :nemesis, :time 1624671425096, :value [:isolated {"175.24.76.99" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "175.24.75.168" #{"175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.76.99" "212.64.86.223"}}], :index 5936}
              {:type :info, :f :stop, :process :nemesis, :time 1644672357674, :index 5964}
              {:type :info, :f :stop, :process :nemesis, :time 1644944664665, :value :network-healed, :index 5967}
              {:type :info, :f :move, :process :nemesis, :time 1654945297694, :index 6006}
              {:type :info, :f :move, :process :nemesis, :time 1654949298594, :error "indeterminate: ", :index 6007}
              {:type :info, :f :move, :process :nemesis, :time 1655449858714, :index 6018}
              {:type :info, :f :move, :process :nemesis, :time 1655451667170, :error "indeterminate: ", :index 6019}
              {:type :info, :f :move, :process :nemesis, :time 1655952120834, :index 6024}
              {:type :info, :f :move, :process :nemesis, :time 1655953795629, :error "indeterminate: ", :index 6025}
              {:type :info, :f :start, :process :nemesis, :time 1656454221733, :index 6030}
              {:type :info, :f :start, :process :nemesis, :time 1656591756996, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.106.227"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227"}}], :index 6031}
              {:type :info, :f :stop, :process :nemesis, :time 1676592101321, :index 6047}
              {:type :info, :f :stop, :process :nemesis, :time 1676875239440, :value :network-healed, :index 6048}
              {:type :info, :f :move, :process :nemesis, :time 1686875803891, :index 6083}
              {:type :info, :f :move, :process :nemesis, :time 1686877492937, :error "indeterminate: ", :index 6084}
              {:type :info, :f :move, :process :nemesis, :time 1687377913661, :index 6090}
              {:type :info, :f :move, :process :nemesis, :time 1687379895583, :error "indeterminate: ", :index 6091}
              {:type :info, :f :move, :process :nemesis, :time 1687880357423, :index 6096}
              {:type :info, :f :move, :process :nemesis, :time 1687881996949, :error "indeterminate: ", :index 6097}
              {:type :info, :f :start, :process :nemesis, :time 1688382380491, :index 6101}
              {:type :info, :f :start, :process :nemesis, :time 1688517637746, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227" "175.24.76.99"}, "175.24.75.168" #{"212.64.58.158" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"212.64.58.158" "212.64.86.223"}}], :index 6108}
              {:type :info, :f :stop, :process :nemesis, :time 1708518640007, :index 6139}
              {:type :info, :f :stop, :process :nemesis, :time 1708782567239, :value :network-healed, :index 6141}
              {:type :info, :f :move, :process :nemesis, :time 1718783067427, :index 6190}
              {:type :info, :f :move, :process :nemesis, :time 1718783879912, :error "indeterminate: ", :index 6191}
              {:type :info, :f :move, :process :nemesis, :time 1719284162030, :index 6195}
              {:type :info, :f :move, :process :nemesis, :time 1719284672523, :error "indeterminate: ", :index 6196}
              {:type :info, :f :move, :process :nemesis, :time 1719784878666, :index 6203}
              {:type :info, :f :move, :process :nemesis, :time 1719786991513, :error "indeterminate: ", :index 6204}
              {:type :info, :f :start, :process :nemesis, :time 1720287472863, :index 6210}
              {:type :info, :f :start, :process :nemesis, :time 1720423567740, :value [:isolated {"175.24.76.99" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "175.24.75.168" #{"175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.76.99" "212.64.86.223"}}], :index 6212}
              {:type :info, :f :stop, :process :nemesis, :time 1740424366538, :index 6241}
              {:type :info, :f :stop, :process :nemesis, :time 1743259523234, :value :network-healed, :index 6257}
              {:type :info, :f :move, :process :nemesis, :time 1753260064188, :index 6324}
              {:type :info, :f :move, :process :nemesis, :time 1753261568118, :error "indeterminate: ", :index 6325}
              {:type :info, :f :move, :process :nemesis, :time 1753761977786, :index 6332}
              {:type :info, :f :move, :process :nemesis, :time 1753763457452, :error "indeterminate: ", :index 6333}
              {:type :info, :f :move, :process :nemesis, :time 1754263854874, :index 6336}
              {:type :info, :f :move, :process :nemesis, :time 1754265288878, :error "indeterminate: ", :index 6337}
              {:type :info, :f :start, :process :nemesis, :time 1754765759880, :index 6344}
              {:type :info, :f :start, :process :nemesis, :time 1754908386188, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.106.227"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227"}}], :index 6345}
              {:type :info, :f :stop, :process :nemesis, :time 1774909211862, :index 6361}
              {:type :info, :f :stop, :process :nemesis, :time 1775183950510, :value :network-healed, :index 6362}
              {:type :info, :f :move, :process :nemesis, :time 1785184414420, :index 6399}
              {:type :info, :f :move, :process :nemesis, :time 1785185115685, :error "indeterminate: ", :index 6400}
              {:type :info, :f :move, :process :nemesis, :time 1785685435384, :index 6402}
              {:type :info, :f :move, :process :nemesis, :time 1785689386637, :error "indeterminate: ", :index 6403}
              {:type :info, :f :move, :process :nemesis, :time 1786189888298, :index 6410}
              {:type :info, :f :move, :process :nemesis, :time 1786191355144, :error "indeterminate: ", :index 6411}
              {:type :info, :f :start, :process :nemesis, :time 1786691752131, :index 6418}
              {:type :info, :f :start, :process :nemesis, :time 1788441953727, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.76.99"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.76.99"}}], :index 6428}
              {:type :info, :f :stop, :process :nemesis, :time 1808442795434, :index 6462}
              {:type :info, :f :stop, :process :nemesis, :time 1808732259881, :value :network-healed, :index 6465}
              {:type :info, :f :move, :process :nemesis, :time 1818732774638, :index 6523}
              {:type :info, :f :move, :process :nemesis, :time 1818734076478, :error "indeterminate: ", :index 6524}
              {:type :info, :f :move, :process :nemesis, :time 1819234454079, :index 6532}
              {:type :info, :f :move, :process :nemesis, :time 1819235982338, :error "indeterminate: ", :index 6533}
              {:type :info, :f :move, :process :nemesis, :time 1819736365091, :index 6535}
              {:type :info, :f :move, :process :nemesis, :time 1819737731557, :error "indeterminate: ", :index 6536}
              {:type :info, :f :start, :process :nemesis, :time 1820238134425, :index 6544}
              {:type :info, :f :start, :process :nemesis, :time 1820376053707, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227" "175.24.76.99"}, "175.24.75.168" #{"212.64.58.158" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"212.64.58.158" "212.64.86.223"}}], :index 6545}
              {:type :info, :f :stop, :process :nemesis, :time 1840376844796, :index 6577}
              {:type :info, :f :stop, :process :nemesis, :time 1843754055601, :value :network-healed, :index 6591}
              {:type :info, :f :move, :process :nemesis, :time 1853754538073, :index 6670}
              {:type :info, :f :move, :process :nemesis, :time 1853756123562, :error "indeterminate: ", :index 6671}
              {:type :info, :f :move, :process :nemesis, :time 1854256561327, :index 6676}
              {:type :info, :f :move, :process :nemesis, :time 1854257267554, :error "indeterminate: ", :index 6677}
              {:type :info, :f :move, :process :nemesis, :time 1854757615540, :index 6680}
              {:type :info, :f :move, :process :nemesis, :time 1854759290026, :error "indeterminate: ", :index 6681}
              {:type :info, :f :start, :process :nemesis, :time 1855259673767, :index 6688}
              {:type :info, :f :start, :process :nemesis, :time 1855395927462, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.75.168" #{"175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.106.227" "175.24.76.99"}}], :index 6690}
              {:type :info, :f :stop, :process :nemesis, :time 1875396830382, :index 6717}
              {:type :info, :f :stop, :process :nemesis, :time 1875671429274, :value :network-healed, :index 6718}
              {:type :info, :f :move, :process :nemesis, :time 1885671953109, :index 6776}
              {:type :info, :f :move, :process :nemesis, :time 1885673523107, :error "indeterminate: ", :index 6777}
              {:type :info, :f :move, :process :nemesis, :time 1886173999001, :index 6786}
              {:type :info, :f :move, :process :nemesis, :time 1886175741481, :error "indeterminate: ", :index 6787}
              {:type :info, :f :move, :process :nemesis, :time 1886676157875, :index 6790}
              {:type :info, :f :move, :process :nemesis, :time 1886677542592, :error "indeterminate: ", :index 6791}
              {:type :info, :f :start, :process :nemesis, :time 1887177944630, :index 6794}
              {:type :info, :f :start, :process :nemesis, :time 1887312607812, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.76.99"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.76.99"}}], :index 6796}
              {:type :info, :f :stop, :process :nemesis, :time 1907313036498, :index 6833}
              {:type :info, :f :stop, :process :nemesis, :time 1909445574490, :value :network-healed, :index 6843}
              {:type :info, :f :move, :process :nemesis, :time 1919446166824, :index 6910}
              {:type :info, :f :move, :process :nemesis, :time 1919447896367, :error "indeterminate: ", :index 6911}
              {:type :info, :f :move, :process :nemesis, :time 1919948340549, :index 6920}
              {:type :info, :f :move, :process :nemesis, :time 1919950019396, :error "indeterminate: ", :index 6921}
              {:type :info, :f :move, :process :nemesis, :time 1920450465586, :index 6924}
              {:type :info, :f :move, :process :nemesis, :time 1920451887956, :error "indeterminate: ", :index 6925}
              {:type :info, :f :start, :process :nemesis, :time 1920952267243, :index 6928}
              {:type :info, :f :start, :process :nemesis, :time 1921085452372, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.75.168" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.86.223"}}], :index 6929}
              {:type :info, :f :stop, :process :nemesis, :time 1941086363045, :index 6949}
              {:type :info, :f :stop, :process :nemesis, :time 1941350567217, :value :network-healed, :index 6950}
              {:type :info, :f :move, :process :nemesis, :time 1951351116327, :index 6987}
              {:type :info, :f :move, :process :nemesis, :time 1951352509176, :error "indeterminate: ", :index 6988}
              {:type :info, :f :move, :process :nemesis, :time 1951852893368, :index 6994}
              {:type :info, :f :move, :process :nemesis, :time 1951855701326, :error "indeterminate: ", :index 6995}
              {:type :info, :f :move, :process :nemesis, :time 1952356249043, :index 6997}
              {:type :info, :f :move, :process :nemesis, :time 1952357599140, :error "indeterminate: ", :index 6998}
              {:type :info, :f :start, :process :nemesis, :time 1952858039767, :index 7006}
              {:type :info, :f :start, :process :nemesis, :time 1952992346939, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.106.227"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227"}}], :index 7007}
              {:type :info, :f :stop, :process :nemesis, :time 1972993140381, :index 7023}
              {:type :info, :f :stop, :process :nemesis, :time 1973258343307, :value :network-healed, :index 7024}
              {:type :info, :f :move, :process :nemesis, :time 1983258916153, :index 7056}
              {:type :info, :f :move, :process :nemesis, :time 1983260280009, :error "indeterminate: ", :index 7057}
              {:type :info, :f :move, :process :nemesis, :time 1983760626174, :index 7062}
              {:type :info, :f :move, :process :nemesis, :time 1983761882968, :error "indeterminate: ", :index 7063}
              {:type :info, :f :move, :process :nemesis, :time 1984262309430, :index 7064}
              {:type :info, :f :move, :process :nemesis, :time 1984263489425, :error "indeterminate: ", :index 7065}
              {:type :info, :f :start, :process :nemesis, :time 1984763921137, :index 7077}
              {:type :info, :f :start, :process :nemesis, :time 1984901363647, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.75.168" #{"175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.106.227" "175.24.76.99"}}], :index 7079}
              {:type :info, :f :stop, :process :nemesis, :time 2004902396923, :index 7109}
              {:type :info, :f :stop, :process :nemesis, :time 2005169156928, :value :network-healed, :index 7110}
              {:type :info, :f :move, :process :nemesis, :time 2015169690318, :index 7148}
              {:type :info, :f :move, :process :nemesis, :time 2015171090077, :error "indeterminate: ", :index 7149}
              {:type :info, :f :move, :process :nemesis, :time 2015671538249, :index 7152}
              {:type :info, :f :move, :process :nemesis, :time 2015673052688, :error "indeterminate: ", :index 7153}
              {:type :info, :f :move, :process :nemesis, :time 2016173538185, :index 7160}
              {:type :info, :f :move, :process :nemesis, :time 2016175155984, :error "indeterminate: ", :index 7161}
              {:type :info, :f :start, :process :nemesis, :time 2016675531872, :index 7166}
              {:type :info, :f :start, :process :nemesis, :time 2016811149077, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.75.168" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.86.223"}}], :index 7170}
              {:type :info, :f :stop, :process :nemesis, :time 2036812115707, :index 7185}
              {:type :info, :f :stop, :process :nemesis, :time 2037080059373, :value :network-healed, :index 7186}
              {:type :info, :f :move, :process :nemesis, :time 2047080659998, :index 7212}
              {:type :info, :f :move, :process :nemesis, :time 2047082469116, :error "indeterminate: ", :index 7213}
              {:type :info, :f :move, :process :nemesis, :time 2047582828794, :index 7214}
              {:type :info, :f :move, :process :nemesis, :time 2047584428967, :error "indeterminate: ", :index 7215}
              {:type :info, :f :move, :process :nemesis, :time 2048084848624, :index 7221}
              {:type :info, :f :move, :process :nemesis, :time 2048086727106, :error "indeterminate: ", :index 7222}
              {:type :info, :f :start, :process :nemesis, :time 2048587181433, :index 7226}
              {:type :info, :f :start, :process :nemesis, :time 2048738400562, :value [:isolated {"175.24.75.168" #{"175.24.106.227" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.58.158"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158"}}], :index 7227}
              {:type :info, :f :stop, :process :nemesis, :time 2068739276644, :index 7243}
              {:type :info, :f :stop, :process :nemesis, :time 2072267307005, :value :network-healed, :index 7244}
              {:type :info, :f :move, :process :nemesis, :time 2082267871626, :index 7297}
              {:type :info, :f :move, :process :nemesis, :time 2082270592139, :error "indeterminate: ", :index 7298}
              {:type :info, :f :move, :process :nemesis, :time 2082771174461, :index 7307}
              {:type :info, :f :move, :process :nemesis, :time 2082771871818, :error "indeterminate: ", :index 7308}
              {:type :info, :f :move, :process :nemesis, :time 2083272163980, :index 7314}
              {:type :info, :f :move, :process :nemesis, :time 2083273787250, :error "indeterminate: ", :index 7315}
              {:type :info, :f :start, :process :nemesis, :time 2083774143590, :index 7320}
              {:type :info, :f :start, :process :nemesis, :time 2083915251289, :value [:isolated {"175.24.76.99" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "175.24.75.168" #{"175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.76.99" "212.64.86.223"}}], :index 7321}
              {:type :info, :f :stop, :process :nemesis, :time 2103915830348, :index 7354}
              {:type :info, :f :stop, :process :nemesis, :time 2104178665006, :value :network-healed, :index 7355}
              {:type :info, :f :move, :process :nemesis, :time 2114179253220, :index 7396}
              {:type :info, :f :move, :process :nemesis, :time 2114181187057, :error "indeterminate: ", :index 7397}
              {:type :info, :f :move, :process :nemesis, :time 2114681599716, :index 7400}
              {:type :info, :f :move, :process :nemesis, :time 2114683172845, :error "indeterminate: ", :index 7401}
              {:type :info, :f :move, :process :nemesis, :time 2115183546054, :index 7404}
              {:type :info, :f :move, :process :nemesis, :time 2115185227108, :error "indeterminate: ", :index 7405}
              {:type :info, :f :start, :process :nemesis, :time 2115685671863, :index 7415}
              {:type :info, :f :start, :process :nemesis, :time 2115821133140, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.75.168" #{"175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.106.227" "175.24.76.99"}}], :index 7418}
              {:type :info, :f :stop, :process :nemesis, :time 2135822040706, :index 7440}
              {:type :info, :f :stop, :process :nemesis, :time 2138224608331, :value :network-healed, :index 7448}
              {:type :info, :f :move, :process :nemesis, :time 2148225205837, :index 7508}
              {:type :info, :f :move, :process :nemesis, :time 2148226948211, :error "indeterminate: ", :index 7509}
              {:type :info, :f :move, :process :nemesis, :time 2148727395780, :index 7512}
              {:type :info, :f :move, :process :nemesis, :time 2148729104329, :error "indeterminate: ", :index 7513}
              {:type :info, :f :move, :process :nemesis, :time 2149229561006, :index 7516}
              {:type :info, :f :move, :process :nemesis, :time 2149231148394, :error "indeterminate: ", :index 7517}
              {:type :info, :f :start, :process :nemesis, :time 2149731596231, :index 7524}
              {:type :info, :f :start, :process :nemesis, :time 2149873985954, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.76.99"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.76.99"}}], :index 7528}
              {:type :info, :f :stop, :process :nemesis, :time 2169874915603, :index 7558}
              {:type :info, :f :stop, :process :nemesis, :time 2170151816599, :value :network-healed, :index 7561}
              {:type :info, :f :move, :process :nemesis, :time 2180152321655, :index 7602}
              {:type :info, :f :move, :process :nemesis, :time 2180152921090, :error "indeterminate: ", :index 7603}
              {:type :info, :f :move, :process :nemesis, :time 2180658570612, :index 7610}
              {:type :info, :f :move, :process :nemesis, :time 2180659171649, :error "indeterminate: ", :index 7611}
              {:type :info, :f :move, :process :nemesis, :time 2181159376235, :index 7616}
              {:type :info, :f :move, :process :nemesis, :time 2181160659856, :error "indeterminate: ", :index 7617}
              {:type :info, :f :start, :process :nemesis, :time 2181660980411, :index 7624}
              {:type :info, :f :start, :process :nemesis, :time 2181796014060, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"212.64.58.158" "175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.76.99"}, "175.24.106.227" #{"175.24.75.168" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "175.24.76.99"}}], :index 7625}
              {:type :info, :f :stop, :process :nemesis, :time 2201796427210, :index 7643}
              {:type :info, :f :stop, :process :nemesis, :time 2202059455589, :value :network-healed, :index 7644}
              {:type :info, :f :move, :process :nemesis, :time 2212060006164, :index 7670}
              {:type :info, :f :move, :process :nemesis, :time 2212061652429, :error "indeterminate: ", :index 7671}
              {:type :info, :f :move, :process :nemesis, :time 2212562061711, :index 7674}
              {:type :info, :f :move, :process :nemesis, :time 2212563707869, :error "indeterminate: ", :index 7675}
              {:type :info, :f :move, :process :nemesis, :time 2213064217896, :index 7681}
              {:type :info, :f :move, :process :nemesis, :time 2213065825229, :error "indeterminate: ", :index 7682}
              {:type :info, :f :start, :process :nemesis, :time 2213566279585, :index 7688}
              {:type :info, :f :start, :process :nemesis, :time 2213699582591, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "175.24.76.99" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.106.227"}, "175.24.76.99" #{"212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227"}}], :index 7689}
              {:type :info, :f :stop, :process :nemesis, :time 2233700564518, :index 7702}
              {:type :info, :f :stop, :process :nemesis, :time 2233964319400, :value :network-healed, :index 7704}
              {:type :info, :f :move, :process :nemesis, :time 2243964816087, :index 7744}
              {:type :info, :f :move, :process :nemesis, :time 2243966105002, :error "indeterminate: ", :index 7745}
              {:type :info, :f :move, :process :nemesis, :time 2244466539319, :index 7756}
              {:type :info, :f :move, :process :nemesis, :time 2244468121726, :error "indeterminate: ", :index 7757}
              {:type :info, :f :move, :process :nemesis, :time 2244968531378, :index 7766}
              {:type :info, :f :move, :process :nemesis, :time 2244970128401, :error "indeterminate: ", :index 7767}
              {:type :info, :f :start, :process :nemesis, :time 2245470505258, :index 7770}
              {:type :info, :f :start, :process :nemesis, :time 2245607202283, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "175.24.75.168" #{"175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.106.227" "212.64.86.223"}}], :index 7772}
              {:type :info, :f :stop, :process :nemesis, :time 2265608049994, :index 7808}
              {:type :info, :f :stop, :process :nemesis, :time 2265885628711, :value :network-healed, :index 7809}
              {:type :info, :f :move, :process :nemesis, :time 2275886140830, :index 7857}
              {:type :info, :f :move, :process :nemesis, :time 2275887732519, :error "indeterminate: ", :index 7858}
              {:type :info, :f :move, :process :nemesis, :time 2276388075484, :index 7868}
              {:type :info, :f :move, :process :nemesis, :time 2276388879643, :error "indeterminate: ", :index 7869}
              {:type :info, :f :move, :process :nemesis, :time 2276889136270, :index 7872}
              {:type :info, :f :move, :process :nemesis, :time 2276890772046, :error "indeterminate: ", :index 7873}
              {:type :info, :f :start, :process :nemesis, :time 2277391210537, :index 7886}
              {:type :info, :f :start, :process :nemesis, :time 2277529380857, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "175.24.75.168" #{"175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.106.227" "212.64.86.223"}}], :index 7890}
              {:type :info, :f :stop, :process :nemesis, :time 2297530223180, :index 7998}
              {:type :info, :f :stop, :process :nemesis, :time 2297795117394, :value :network-healed, :index 7999}
              {:type :info, :f :move, :process :nemesis, :time 2307795654173, :index 8062}
              {:type :info, :f :move, :process :nemesis, :time 2307797506576, :error "indeterminate: ", :index 8063}
              {:type :info, :f :move, :process :nemesis, :time 2308297910127, :index 8070}
              {:type :info, :f :move, :process :nemesis, :time 2308300075540, :error "indeterminate: ", :index 8071}
              {:type :info, :f :move, :process :nemesis, :time 2308800578357, :index 8074}
              {:type :info, :f :move, :process :nemesis, :time 2308802124254, :error "indeterminate: ", :index 8075}
              {:type :info, :f :start, :process :nemesis, :time 2309302518885, :index 8078}
              {:type :info, :f :start, :process :nemesis, :time 2309446331394, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227" "175.24.76.99"}, "175.24.75.168" #{"212.64.58.158" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"212.64.58.158" "212.64.86.223"}}], :index 8079}
              {:type :info, :f :stop, :process :nemesis, :time 2329447166467, :index 8107}
              {:type :info, :f :stop, :process :nemesis, :time 2329714021306, :value :network-healed, :index 8108}
              {:type :info, :f :move, :process :nemesis, :time 2339714580562, :index 8162}
              {:type :info, :f :move, :process :nemesis, :time 2339716122607, :error "indeterminate: ", :index 8163}
              {:type :info, :f :move, :process :nemesis, :time 2340216559245, :index 8167}
              {:type :info, :f :move, :process :nemesis, :time 2340218176395, :error "indeterminate: ", :index 8168}
              {:type :info, :f :move, :process :nemesis, :time 2340718593876, :index 8173}
              {:type :info, :f :move, :process :nemesis, :time 2340720166840, :error "indeterminate: ", :index 8174}
              {:type :info, :f :start, :process :nemesis, :time 2341220597255, :index 8182}
              {:type :info, :f :start, :process :nemesis, :time 2341356065316, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.76.99"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.76.99"}}], :index 8183}
              {:type :info, :f :stop, :process :nemesis, :time 2361356932410, :index 8216}
              {:type :info, :f :stop, :process :nemesis, :time 2361620540456, :value :network-healed, :index 8217}
              {:type :info, :f :move, :process :nemesis, :time 2371621144123, :index 8263}
              {:type :info, :f :move, :process :nemesis, :time 2371622698374, :error "indeterminate: ", :index 8264}
              {:type :info, :f :move, :process :nemesis, :time 2372123116858, :index 8268}
              {:type :info, :f :move, :process :nemesis, :time 2372124952516, :error "indeterminate: ", :index 8269}
              {:type :info, :f :move, :process :nemesis, :time 2372625381186, :index 8270}
              {:type :info, :f :move, :process :nemesis, :time 2372627037766, :error "indeterminate: ", :index 8271}
              {:type :info, :f :start, :process :nemesis, :time 2373127470072, :index 8276}
              {:type :info, :f :start, :process :nemesis, :time 2373274357229, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "175.24.76.99" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.106.227"}, "175.24.76.99" #{"212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227"}}], :index 8280}
              {:type :info, :f :stop, :process :nemesis, :time 2393275321058, :index 8390}
              {:type :info, :f :stop, :process :nemesis, :time 2393540359009, :value :network-healed, :index 8391}
              {:type :info, :f :move, :process :nemesis, :time 2403540881901, :index 8455}
              {:type :info, :f :move, :process :nemesis, :time 2403542538081, :error "indeterminate: ", :index 8456}
              {:type :info, :f :move, :process :nemesis, :time 2404042973796, :index 8466}
              {:type :info, :f :move, :process :nemesis, :time 2404044856821, :error "indeterminate: ", :index 8467}
              {:type :info, :f :move, :process :nemesis, :time 2404545308302, :index 8470}
              {:type :info, :f :move, :process :nemesis, :time 2404546709334, :error "indeterminate: ", :index 8471}
              {:type :info, :f :start, :process :nemesis, :time 2405047059484, :index 8482}
              {:type :info, :f :start, :process :nemesis, :time 2405180753368, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "175.24.76.99" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.106.227"}, "175.24.76.99" #{"212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227"}}], :index 8483}
              {:type :info, :f :stop, :process :nemesis, :time 2425181867619, :index 8621}
              {:type :info, :f :stop, :process :nemesis, :time 2425446221922, :value :network-healed, :index 8623}
              {:type :info, :f :move, :process :nemesis, :time 2435446788618, :index 8684}
              {:type :info, :f :move, :process :nemesis, :time 2435448347531, :error "indeterminate: ", :index 8685}
              {:type :info, :f :move, :process :nemesis, :time 2435948749145, :index 8687}
              {:type :info, :f :move, :process :nemesis, :time 2435950394560, :error "indeterminate: ", :index 8688}
              {:type :info, :f :move, :process :nemesis, :time 2436450794123, :index 8695}
              {:type :info, :f :move, :process :nemesis, :time 2436452457275, :error "indeterminate: ", :index 8696}
              {:type :info, :f :start, :process :nemesis, :time 2436952889262, :index 8702}
              {:type :info, :f :start, :process :nemesis, :time 2437093811056, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.106.227"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227"}}], :index 8705}
              {:type :info, :f :stop, :process :nemesis, :time 2457094691122, :index 8723}
              {:type :info, :f :stop, :process :nemesis, :time 2457381548487, :value :network-healed, :index 8724}
              {:type :info, :f :move, :process :nemesis, :time 2467382071458, :index 8747}
              {:type :info, :f :move, :process :nemesis, :time 2467384047546, :error "indeterminate: ", :index 8748}
              {:type :info, :f :move, :process :nemesis, :time 2467884510298, :index 8753}
              {:type :info, :f :move, :process :nemesis, :time 2467886355171, :error "indeterminate: ", :index 8754}
              {:type :info, :f :move, :process :nemesis, :time 2468386815005, :index 8756}
              {:type :info, :f :move, :process :nemesis, :time 2468388872335, :error "indeterminate: ", :index 8757}
              {:type :info, :f :start, :process :nemesis, :time 2468889382974, :index 8763}
              {:type :info, :f :start, :process :nemesis, :time 2469026778728, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "175.24.75.168" #{"175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.106.227" "212.64.86.223"}}], :index 8765}
              {:type :info, :f :stop, :process :nemesis, :time 2489028047814, :index 8888}
              {:type :info, :f :stop, :process :nemesis, :time 2489293821869, :value :network-healed, :index 8890}
              {:type :info, :f :move, :process :nemesis, :time 2499294560822, :index 8965}
              {:type :info, :f :move, :process :nemesis, :time 2499296446883, :error "indeterminate: ", :index 8966}
              {:type :info, :f :move, :process :nemesis, :time 2499797086082, :index 8969}
              {:type :info, :f :move, :process :nemesis, :time 2499798775792, :error "indeterminate: ", :index 8970}
              {:type :info, :f :move, :process :nemesis, :time 2500299198780, :index 8980}
              {:type :info, :f :move, :process :nemesis, :time 2500301205064, :error "indeterminate: ", :index 8981}
              {:type :info, :f :start, :process :nemesis, :time 2500801643523, :index 8988}
              {:type :info, :f :start, :process :nemesis, :time 2500939262359, :value [:isolated {"175.24.76.99" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "175.24.75.168" #{"175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.76.99" "212.64.86.223"}}], :index 8989}
              {:type :info, :f :stop, :process :nemesis, :time 2520940243256, :index 9124}
              {:type :info, :f :stop, :process :nemesis, :time 2521212659737, :value :network-healed, :index 9125}
              {:type :info, :f :move, :process :nemesis, :time 2531213185524, :index 9192}
              {:type :info, :f :move, :process :nemesis, :time 2531214758109, :error "indeterminate: ", :index 9193}
              {:type :info, :f :move, :process :nemesis, :time 2531715163360, :index 9198}
              {:type :info, :f :move, :process :nemesis, :time 2531716785808, :error "indeterminate: ", :index 9199}
              {:type :info, :f :move, :process :nemesis, :time 2532217199280, :index 9213}
              {:type :info, :f :move, :process :nemesis, :time 2532219950947, :error "indeterminate: ", :index 9214}
              {:type :info, :f :start, :process :nemesis, :time 2532720515670, :index 9216}
              {:type :info, :f :start, :process :nemesis, :time 2532854915038, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"212.64.58.158" "175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.76.99"}, "175.24.106.227" #{"175.24.75.168" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "175.24.76.99"}}], :index 9218}
              {:type :info, :f :stop, :process :nemesis, :time 2552855967896, :index 9233}
              {:type :info, :f :stop, :process :nemesis, :time 2553118434752, :value :network-healed, :index 9234}
              {:type :info, :f :move, :process :nemesis, :time 2563119027379, :index 9255}
              {:type :info, :f :move, :process :nemesis, :time 2563120687759, :error "indeterminate: ", :index 9256}
              {:type :info, :f :move, :process :nemesis, :time 2563621138108, :index 9258}
              {:type :info, :f :move, :process :nemesis, :time 2563622831591, :error "indeterminate: ", :index 9259}
              {:type :info, :f :move, :process :nemesis, :time 2564123278967, :index 9264}
              {:type :info, :f :move, :process :nemesis, :time 2564125453008, :error "indeterminate: ", :index 9265}
              {:type :info, :f :start, :process :nemesis, :time 2564625912146, :index 9270}
              {:type :info, :f :start, :process :nemesis, :time 2564759941716, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "175.24.76.99" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.106.227"}, "175.24.76.99" #{"212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227"}}], :index 9273}
              {:type :info, :f :stop, :process :nemesis, :time 2584761077902, :index 9306}
              {:type :info, :f :stop, :process :nemesis, :time 2585023099529, :value :network-healed, :index 9310}
              {:type :info, :f :move, :process :nemesis, :time 2595023660048, :index 9366}
              {:type :info, :f :move, :process :nemesis, :time 2595025280723, :error "indeterminate: ", :index 9367}
              {:type :info, :f :move, :process :nemesis, :time 2595525681253, :index 9368}
              {:type :info, :f :move, :process :nemesis, :time 2595527301627, :error "indeterminate: ", :index 9369}
              {:type :info, :f :move, :process :nemesis, :time 2596027684813, :index 9376}
              {:type :info, :f :move, :process :nemesis, :time 2596029732101, :error "indeterminate: ", :index 9377}
              {:type :info, :f :start, :process :nemesis, :time 2596530146228, :index 9382}
              {:type :info, :f :start, :process :nemesis, :time 2596665883593, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"212.64.58.158" "175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.76.99"}, "175.24.106.227" #{"175.24.75.168" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "175.24.76.99"}}], :index 9383}
              {:type :info, :f :stop, :process :nemesis, :time 2616666877348, :index 9401}
              {:type :info, :f :stop, :process :nemesis, :time 2616942075032, :value :network-healed, :index 9402}
              {:type :info, :f :move, :process :nemesis, :time 2626942697210, :index 9429}
              {:type :info, :f :move, :process :nemesis, :time 2626944801392, :error "indeterminate: ", :index 9430}
              {:type :info, :f :move, :process :nemesis, :time 2627445305504, :index 9432}
              {:type :info, :f :move, :process :nemesis, :time 2627446947669, :error "indeterminate: ", :index 9433}
              {:type :info, :f :move, :process :nemesis, :time 2627947357334, :index 9434}
              {:type :info, :f :move, :process :nemesis, :time 2627950929586, :error "indeterminate: ", :index 9435}
              {:type :info, :f :start, :process :nemesis, :time 2628451209898, :index 9441}
              {:type :info, :f :start, :process :nemesis, :time 2628583088664, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227" "175.24.76.99"}, "175.24.75.168" #{"212.64.58.158" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"212.64.58.158" "212.64.86.223"}}], :index 9445}
              {:type :info, :f :stop, :process :nemesis, :time 2648583918364, :index 9488}
              {:type :info, :f :stop, :process :nemesis, :time 2648851061539, :value :network-healed, :index 9489}
              {:type :info, :f :move, :process :nemesis, :time 2658851605301, :index 9572}
              {:type :info, :f :move, :process :nemesis, :time 2658853168169, :error "indeterminate: ", :index 9573}
              {:type :info, :f :move, :process :nemesis, :time 2659353491177, :index 9580}
              {:type :info, :f :move, :process :nemesis, :time 2659354066671, :error "indeterminate: ", :index 9581}
              {:type :info, :f :move, :process :nemesis, :time 2659854347711, :index 9585}
              {:type :info, :f :move, :process :nemesis, :time 2659855954714, :error "indeterminate: ", :index 9586}
              {:type :info, :f :start, :process :nemesis, :time 2660356353546, :index 9590}
              {:type :info, :f :start, :process :nemesis, :time 2663507643467, :value [:isolated {"175.24.75.168" #{"175.24.106.227" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.58.158"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158"}}], :index 9599}
              {:type :info, :f :stop, :process :nemesis, :time 2683508505822, :index 9609}
              {:type :info, :f :stop, :process :nemesis, :time 2683773829980, :value :network-healed, :index 9610}
              {:type :info, :f :move, :process :nemesis, :time 2693774394097, :index 9652}
              {:type :info, :f :move, :process :nemesis, :time 2693776310293, :error "indeterminate: ", :index 9653}
              {:type :info, :f :move, :process :nemesis, :time 2694276763430, :index 9658}
              {:type :info, :f :move, :process :nemesis, :time 2694277478383, :error "indeterminate: ", :index 9659}
              {:type :info, :f :move, :process :nemesis, :time 2694777765530, :index 9662}
              {:type :info, :f :move, :process :nemesis, :time 2694779377189, :error "indeterminate: ", :index 9663}
              {:type :info, :f :start, :process :nemesis, :time 2695279799489, :index 9667}
              {:type :info, :f :start, :process :nemesis, :time 2695413865738, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"212.64.58.158" "175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.76.99"}, "175.24.106.227" #{"175.24.75.168" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "175.24.76.99"}}], :index 9669}
              {:type :info, :f :stop, :process :nemesis, :time 2715414234451, :index 9692}
              {:type :info, :f :stop, :process :nemesis, :time 2715676410220, :value :network-healed, :index 9693}
              {:type :info, :f :move, :process :nemesis, :time 2725676937747, :index 9746}
              {:type :info, :f :move, :process :nemesis, :time 2725678376829, :error "indeterminate: ", :index 9747}
              {:type :info, :f :move, :process :nemesis, :time 2726178735663, :index 9750}
              {:type :info, :f :move, :process :nemesis, :time 2726180331281, :error "indeterminate: ", :index 9751}
              {:type :info, :f :move, :process :nemesis, :time 2726680722529, :index 9754}
              {:type :info, :f :move, :process :nemesis, :time 2726682312878, :error "indeterminate: ", :index 9755}
              {:type :info, :f :start, :process :nemesis, :time 2727182674223, :index 9758}
              {:type :info, :f :start, :process :nemesis, :time 2727423524896, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.75.168" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.86.223"}}], :index 9762}
              {:type :info, :f :stop, :process :nemesis, :time 2747424351913, :index 9775}
              {:type :info, :f :stop, :process :nemesis, :time 2747729478150, :value :network-healed, :index 9776}
              {:type :info, :f :move, :process :nemesis, :time 2757730012508, :index 9796}
              {:type :info, :f :move, :process :nemesis, :time 2757732112940, :error "indeterminate: ", :index 9797}
              {:type :info, :f :move, :process :nemesis, :time 2758232645131, :index 9802}
              {:type :info, :f :move, :process :nemesis, :time 2758234591963, :error "indeterminate: ", :index 9803}
              {:type :info, :f :move, :process :nemesis, :time 2758735101465, :index 9810}
              {:type :info, :f :move, :process :nemesis, :time 2758737068692, :error "indeterminate: ", :index 9811}
              {:type :info, :f :start, :process :nemesis, :time 2759237514732, :index 9822}
              {:type :info, :f :start, :process :nemesis, :time 2762426035912, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.75.168" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.86.223"}}], :index 9840}
              {:type :info, :f :stop, :process :nemesis, :time 2782426859367, :index 9855}
              {:type :info, :f :stop, :process :nemesis, :time 2783043483031, :value :network-healed, :index 9856}
              {:type :info, :f :move, :process :nemesis, :time 2793044019644, :index 9894}
              {:type :info, :f :move, :process :nemesis, :time 2793045361062, :error "indeterminate: ", :index 9895}
              {:type :info, :f :move, :process :nemesis, :time 2793545902395, :index 9899}
              {:type :info, :f :move, :process :nemesis, :time 2793547121810, :error "indeterminate: ", :index 9900}
              {:type :info, :f :move, :process :nemesis, :time 2794047464833, :index 9905}
              {:type :info, :f :move, :process :nemesis, :time 2794048882606, :error "indeterminate: ", :index 9906}
              {:type :info, :f :start, :process :nemesis, :time 2794549226887, :index 9909}
              {:type :info, :f :start, :process :nemesis, :time 2794763573948, :value [:isolated {"175.24.75.168" #{"175.24.106.227" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.58.158"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158"}}], :index 9911}
              {:type :info, :f :stop, :process :nemesis, :time 2814764259743, :index 9929}
              {:type :info, :f :stop, :process :nemesis, :time 2826132501107, :value :network-healed, :index 9959}
              {:type :info, :f :move, :process :nemesis, :time 2836132717593, :index 10024}
              {:type :info, :f :move, :process :nemesis, :time 2836133293883, :error "indeterminate: ", :index 10025}
              {:type :info, :f :move, :process :nemesis, :time 2836633552901, :index 10028}
              {:type :info, :f :move, :process :nemesis, :time 2836635891013, :error "indeterminate: ", :index 10029}
              {:type :info, :f :move, :process :nemesis, :time 2837136347636, :index 10034}
              {:type :info, :f :move, :process :nemesis, :time 2837138744799, :error "indeterminate: ", :index 10035}
              {:type :info, :f :start, :process :nemesis, :time 2837639250376, :index 10040}
              {:type :info, :f :start, :process :nemesis, :time 2837915443160, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.76.99"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.76.99"}}], :index 10045}
              {:type :info, :f :stop, :process :nemesis, :time 2857915873334, :index 10144}
              {:type :info, :f :stop, :process :nemesis, :time 2858192961010, :value :network-healed, :index 10151}
              {:type :info, :f :move, :process :nemesis, :time 2868193509574, :index 10216}
              {:type :info, :f :move, :process :nemesis, :time 2868194815487, :error "indeterminate: ", :index 10217}
              {:type :info, :f :move, :process :nemesis, :time 2868695201242, :index 10222}
              {:type :info, :f :move, :process :nemesis, :time 2868696489554, :error "indeterminate: ", :index 10223}
              {:type :info, :f :move, :process :nemesis, :time 2869196840387, :index 10226}
              {:type :info, :f :move, :process :nemesis, :time 2869198099145, :error "indeterminate: ", :index 10227}
              {:type :info, :f :start, :process :nemesis, :time 2869698372461, :index 10234}
              {:type :info, :f :start, :process :nemesis, :time 2869829776928, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227" "175.24.76.99"}, "175.24.75.168" #{"212.64.58.158" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"212.64.58.158" "212.64.86.223"}}], :index 10236}
              {:type :info, :f :stop, :process :nemesis, :time 2889830464064, :index 10346}
              {:type :info, :f :stop, :process :nemesis, :time 2890110144420, :value :network-healed, :index 10351}
              {:type :info, :f :move, :process :nemesis, :time 2900110411497, :index 10418}
              {:type :info, :f :move, :process :nemesis, :time 2900110957553, :error "indeterminate: ", :index 10419}
              {:type :info, :f :move, :process :nemesis, :time 2900611166648, :index 10422}
              {:type :info, :f :move, :process :nemesis, :time 2900611707858, :error "indeterminate: ", :index 10423}
              {:type :info, :f :move, :process :nemesis, :time 2901111970970, :index 10428}
              {:type :info, :f :move, :process :nemesis, :time 2901113384022, :error "indeterminate: ", :index 10429}
              {:type :info, :f :start, :process :nemesis, :time 2901613664214, :index 10433}
              {:type :info, :f :start, :process :nemesis, :time 2901746883049, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.106.227"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227"}}], :index 10437}
              {:type :info, :f :stop, :process :nemesis, :time 2921747246458, :index 10451}
              {:type :info, :f :stop, :process :nemesis, :time 2922015537609, :value :network-healed, :index 10452}
              {:type :info, :f :move, :process :nemesis, :time 2932015874718, :index 10484}
              {:type :info, :f :move, :process :nemesis, :time 2932016526797, :error "indeterminate: ", :index 10485}
              {:type :info, :f :move, :process :nemesis, :time 2932516747838, :index 10496}
              {:type :info, :f :move, :process :nemesis, :time 2932517531186, :error "indeterminate: ", :index 10497}
              {:type :info, :f :move, :process :nemesis, :time 2933017873994, :index 10501}
              {:type :info, :f :move, :process :nemesis, :time 2933019839879, :error "indeterminate: ", :index 10502}
              {:type :info, :f :start, :process :nemesis, :time 2933520222206, :index 10504}
              {:type :info, :f :start, :process :nemesis, :time 2933860090798, :value [:isolated {"175.24.75.168" #{"175.24.106.227" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.58.158"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158"}}], :index 10506}
              {:type :info, :f :stop, :process :nemesis, :time 2953861889071, :index 10532}
              {:type :info, :f :stop, :process :nemesis, :time 2956763147178, :value :network-healed, :index 10536}
              {:type :info, :f :move, :process :nemesis, :time 2966763642219, :index 10599}
              {:type :info, :f :move, :process :nemesis, :time 2966765190601, :error "indeterminate: ", :index 10600}
              {:type :info, :f :move, :process :nemesis, :time 2967265566410, :index 10608}
              {:type :info, :f :move, :process :nemesis, :time 2967266508830, :error "indeterminate: ", :index 10609}
              {:type :info, :f :move, :process :nemesis, :time 2967766820075, :index 10612}
              {:type :info, :f :move, :process :nemesis, :time 2967768197073, :error "indeterminate: ", :index 10613}
              {:type :info, :f :start, :process :nemesis, :time 2968268518992, :index 10620}
              {:type :info, :f :start, :process :nemesis, :time 2968470053208, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.106.227"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227"}}], :index 10624}
              {:type :info, :f :stop, :process :nemesis, :time 2988470718916, :index 10639}
              {:type :info, :f :stop, :process :nemesis, :time 2992442507088, :value :network-healed, :index 10640}
              {:type :info, :f :move, :process :nemesis, :time 3002442960307, :index 10681}
              {:type :info, :f :move, :process :nemesis, :time 3002445452259, :error "indeterminate: ", :index 10682}
              {:type :info, :f :move, :process :nemesis, :time 3002946029617, :index 10686}
              {:type :info, :f :move, :process :nemesis, :time 3002947722872, :error "indeterminate: ", :index 10687}
              {:type :info, :f :move, :process :nemesis, :time 3003448111067, :index 10691}
              {:type :info, :f :move, :process :nemesis, :time 3003449472274, :error "indeterminate: ", :index 10692}
              {:type :info, :f :start, :process :nemesis, :time 3003949743888, :index 10701}
              {:type :info, :f :start, :process :nemesis, :time 3006256418269, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.76.99"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.76.99"}}], :index 10709}
              {:type :info, :f :stop, :process :nemesis, :time 3026256812789, :index 10732}
              {:type :info, :f :stop, :process :nemesis, :time 3027601961123, :value :network-healed, :index 10736}
              {:type :info, :f :move, :process :nemesis, :time 3037602382706, :index 10790}
              {:type :info, :f :move, :process :nemesis, :time 3037602972899, :error "indeterminate: ", :index 10791}
              {:type :info, :f :move, :process :nemesis, :time 3038103291344, :index 10800}
              {:type :info, :f :move, :process :nemesis, :time 3038105565620, :error "indeterminate: ", :index 10801}
              {:type :info, :f :move, :process :nemesis, :time 3038605949993, :index 10804}
              {:type :info, :f :move, :process :nemesis, :time 3038606412146, :error "indeterminate: ", :index 10805}
              {:type :info, :f :start, :process :nemesis, :time 3039106579439, :index 10810}
              {:type :info, :f :start, :process :nemesis, :time 3039312899173, :value [:isolated {"175.24.75.168" #{"175.24.106.227" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.58.158"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158"}}], :index 10813}
              {:type :info, :f :stop, :process :nemesis, :time 3059313316965, :index 10831}
              {:type :info, :f :stop, :process :nemesis, :time 3066437108902, :value :network-healed, :index 10837}
              {:type :info, :f :move, :process :nemesis, :time 3076438043139, :index 10923}
              {:type :info, :f :move, :process :nemesis, :time 3076438725407, :error "indeterminate: ", :index 10924}
              {:type :info, :f :move, :process :nemesis, :time 3076938916021, :index 10928}
              {:type :info, :f :move, :process :nemesis, :time 3076939572617, :error "indeterminate: ", :index 10929}
              {:type :info, :f :move, :process :nemesis, :time 3077439843882, :index 10934}
              {:type :info, :f :move, :process :nemesis, :time 3077441351064, :error "indeterminate: ", :index 10935}
              {:type :info, :f :start, :process :nemesis, :time 3077941968857, :index 10944}
              {:type :info, :f :start, :process :nemesis, :time 3078109696004, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.106.227"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227"}}], :index 10946}
              {:type :info, :f :stop, :process :nemesis, :time 3098110157145, :index 10961}
              {:type :info, :f :stop, :process :nemesis, :time 3100498316920, :value :network-healed, :index 10962}
              {:type :info, :f :move, :process :nemesis, :time 3110498593869, :index 10993}
              {:type :info, :f :move, :process :nemesis, :time 3110499385767, :error "indeterminate: ", :index 10994}
              {:type :info, :f :move, :process :nemesis, :time 3110999635604, :index 11001}
              {:type :info, :f :move, :process :nemesis, :time 3111001113667, :error "indeterminate: ", :index 11002}
              {:type :info, :f :move, :process :nemesis, :time 3111501373889, :index 11009}
              {:type :info, :f :move, :process :nemesis, :time 3111501879001, :error "indeterminate: ", :index 11010}
              {:type :info, :f :start, :process :nemesis, :time 3112002284752, :index 11014}
              {:type :info, :f :start, :process :nemesis, :time 3112211235630, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "175.24.75.168" #{"175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.106.227" "212.64.86.223"}}], :index 11015}
              {:type :info, :f :stop, :process :nemesis, :time 3132211597004, :index 11056}
              {:type :info, :f :stop, :process :nemesis, :time 3132766017362, :value :network-healed, :index 11058}
              {:type :info, :f :move, :process :nemesis, :time 3142766225652, :index 11120}
              {:type :info, :f :move, :process :nemesis, :time 3142766671454, :error "indeterminate: ", :index 11121}
              {:type :info, :f :move, :process :nemesis, :time 3143268110527, :index 11124}
              {:type :info, :f :move, :process :nemesis, :time 3143268821067, :error "indeterminate: ", :index 11125}
              {:type :info, :f :move, :process :nemesis, :time 3143769013823, :index 11128}
              {:type :info, :f :move, :process :nemesis, :time 3143769618526, :error "indeterminate: ", :index 11129}
              {:type :info, :f :start, :process :nemesis, :time 3144269799819, :index 11134}
              {:type :info, :f :start, :process :nemesis, :time 3144474731144, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.106.227"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227"}}], :index 11137}
              {:type :info, :f :stop, :process :nemesis, :time 3164475323928, :index 11155}
              {:type :info, :f :stop, :process :nemesis, :time 3164802835134, :value :network-healed, :index 11156}
              {:type :info, :f :move, :process :nemesis, :time 3174803092316, :index 11183}
              {:type :info, :f :move, :process :nemesis, :time 3174803737972, :error "indeterminate: ", :index 11184}
              {:type :info, :f :move, :process :nemesis, :time 3175303951433, :index 11193}
              {:type :info, :f :move, :process :nemesis, :time 3175305331193, :error "indeterminate: ", :index 11194}
              {:type :info, :f :move, :process :nemesis, :time 3175805549398, :index 11198}
              {:type :info, :f :move, :process :nemesis, :time 3175806303674, :error "indeterminate: ", :index 11199}
              {:type :info, :f :start, :process :nemesis, :time 3176306488100, :index 11205}
              {:type :info, :f :start, :process :nemesis, :time 3176560425831, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"212.64.58.158" "175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.76.99"}, "175.24.106.227" #{"175.24.75.168" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "175.24.76.99"}}], :index 11208}
              {:type :info, :f :stop, :process :nemesis, :time 3196560947153, :index 11234}
              {:type :info, :f :stop, :process :nemesis, :time 3196837616877, :value :network-healed, :index 11235}
              {:type :info, :f :move, :process :nemesis, :time 3206838038114, :index 11276}
              {:type :info, :f :move, :process :nemesis, :time 3206842052462, :error "indeterminate: ", :index 11277}
              {:type :info, :f :move, :process :nemesis, :time 3207342593601, :index 11284}
              {:type :info, :f :move, :process :nemesis, :time 3207343619478, :error "indeterminate: ", :index 11285}
              {:type :info, :f :move, :process :nemesis, :time 3207843850638, :index 11288}
              {:type :info, :f :move, :process :nemesis, :time 3207844376642, :error "indeterminate: ", :index 11289}
              {:type :info, :f :start, :process :nemesis, :time 3208344559441, :index 11294}
              {:type :info, :f :start, :process :nemesis, :time 3208506053729, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "175.24.76.99" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.106.227"}, "175.24.76.99" #{"212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227"}}], :index 11295}
              {:type :info, :f :stop, :process :nemesis, :time 3228506371949, :index 11324}
              {:type :info, :f :stop, :process :nemesis, :time 3228810366387, :value :network-healed, :index 11325}
              {:type :info, :f :move, :process :nemesis, :time 3238810631735, :index 11394}
              {:type :info, :f :move, :process :nemesis, :time 3238811154605, :error "indeterminate: ", :index 11395}
              {:type :info, :f :move, :process :nemesis, :time 3239311300413, :index 11399}
              {:type :info, :f :move, :process :nemesis, :time 3239311921273, :error "indeterminate: ", :index 11400}
              {:type :info, :f :move, :process :nemesis, :time 3239812108068, :index 11406}
              {:type :info, :f :move, :process :nemesis, :time 3239812697708, :error "indeterminate: ", :index 11407}
              {:type :info, :f :start, :process :nemesis, :time 3240312861563, :index 11414}
              {:type :info, :f :start, :process :nemesis, :time 3240464923490, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.75.168" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.86.223"}}], :index 11415}
              {:type :info, :f :stop, :process :nemesis, :time 3260465319949, :index 11429}
              {:type :info, :f :stop, :process :nemesis, :time 3261095654661, :value :network-healed, :index 11430}
              {:type :info, :f :move, :process :nemesis, :time 3271095879490, :index 11466}
              {:type :info, :f :move, :process :nemesis, :time 3271096625394, :error "indeterminate: ", :index 11467}
              {:type :info, :f :move, :process :nemesis, :time 3271596796509, :index 11470}
              {:type :info, :f :move, :process :nemesis, :time 3271597495257, :error "indeterminate: ", :index 11471}
              {:type :info, :f :move, :process :nemesis, :time 3272097653332, :index 11476}
              {:type :info, :f :move, :process :nemesis, :time 3272098171761, :error "indeterminate: ", :index 11477}
              {:type :info, :f :start, :process :nemesis, :time 3272598325254, :index 11482}
              {:type :info, :f :start, :process :nemesis, :time 3272746716322, :value [:isolated {"175.24.75.168" #{"175.24.106.227" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.75.168" "212.64.58.158"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158"}}], :index 11483}
              {:type :info, :f :stop, :process :nemesis, :time 3292747094731, :index 11509}
              {:type :info, :f :stop, :process :nemesis, :time 3298447845106, :value :network-healed, :index 11512}
              {:type :info, :f :move, :process :nemesis, :time 3308448137539, :index 11554}
              {:type :info, :f :move, :process :nemesis, :time 3308448797927, :error "indeterminate: ", :index 11555}
              {:type :info, :f :move, :process :nemesis, :time 3308948995225, :index 11559}
              {:type :info, :f :move, :process :nemesis, :time 3308949572319, :error "indeterminate: ", :index 11560}
              {:type :info, :f :move, :process :nemesis, :time 3309449751236, :index 11563}
              {:type :info, :f :move, :process :nemesis, :time 3309450394762, :error "indeterminate: ", :index 11564}
              {:type :info, :f :start, :process :nemesis, :time 3309950563788, :index 11567}
              {:type :info, :f :start, :process :nemesis, :time 3313029625948, :value [:isolated {"175.24.75.168" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.75.168" "175.24.106.227"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227"}}], :index 11584}
              {:type :info, :f :stop, :process :nemesis, :time 3333030133960, :index 11637}
              {:type :info, :f :stop, :process :nemesis, :time 3333316543872, :value :network-healed, :index 11639}
              {:type :info, :f :move, :process :nemesis, :time 3343316775551, :index 11704}
              {:type :info, :f :move, :process :nemesis, :time 3343318109705, :error "indeterminate: ", :index 11705}
              {:type :info, :f :move, :process :nemesis, :time 3343818432196, :index 11708}
              {:type :info, :f :move, :process :nemesis, :time 3343821597037, :error "indeterminate: ", :index 11709}
              {:type :info, :f :move, :process :nemesis, :time 3344322132486, :index 11715}
              {:type :info, :f :move, :process :nemesis, :time 3344322986743, :error "indeterminate: ", :index 11716}
              {:type :info, :f :start, :process :nemesis, :time 3344823245132, :index 11722}
              {:type :info, :f :start, :process :nemesis, :time 3344963507970, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.75.168" #{"175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.106.227" "175.24.76.99"}}], :index 11723}
              {:type :info, :f :stop, :process :nemesis, :time 3364964567847, :index 11848}
              {:type :info, :f :stop, :process :nemesis, :time 3365226585126, :value :network-healed, :index 11851}
              {:type :info, :f :move, :process :nemesis, :time 3375226925690, :index 11930}
              {:type :info, :f :move, :process :nemesis, :time 3375228911566, :error "indeterminate: ", :index 11931}
              {:type :info, :f :move, :process :nemesis, :time 3375729332863, :index 11937}
              {:type :info, :f :move, :process :nemesis, :time 3375731486984, :error "indeterminate: ", :index 11938}
              {:type :info, :f :move, :process :nemesis, :time 3376231920570, :index 11942}
              {:type :info, :f :move, :process :nemesis, :time 3376233849347, :error "indeterminate: ", :index 11943}
              {:type :info, :f :start, :process :nemesis, :time 3376734320705, :index 11950}
              {:type :info, :f :start, :process :nemesis, :time 3376867873245, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "175.24.75.168" #{"175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.106.227" "212.64.86.223"}}], :index 11951}
              {:type :info, :f :stop, :process :nemesis, :time 3396868791622, :index 11990}
              {:type :info, :f :stop, :process :nemesis, :time 3397170095725, :value :network-healed, :index 11991}
              {:type :info, :f :move, :process :nemesis, :time 3407170397818, :index 12044}
              {:type :info, :f :move, :process :nemesis, :time 3407171192887, :error "indeterminate: ", :index 12045}
              {:type :info, :f :move, :process :nemesis, :time 3407671398099, :index 12053}
              {:type :info, :f :move, :process :nemesis, :time 3407671950469, :error "indeterminate: ", :index 12054}
              {:type :info, :f :move, :process :nemesis, :time 3408172155164, :index 12058}
              {:type :info, :f :move, :process :nemesis, :time 3408172964744, :error "indeterminate: ", :index 12059}
              {:type :info, :f :start, :process :nemesis, :time 3408673160391, :index 12060}
              {:type :info, :f :start, :process :nemesis, :time 3408828893050, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.76.99"}, "175.24.75.168" #{"175.24.106.227" "212.64.86.223"}, "212.64.58.158" #{"175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.106.227" "212.64.86.223"}}], :index 12062}
              {:type :info, :f :stop, :process :nemesis, :time 3428829417173, :index 12184}
              {:type :info, :f :stop, :process :nemesis, :time 3429099789404, :value :network-healed, :index 12187}
              {:type :info, :f :move, :process :nemesis, :time 3439100015350, :index 12246}
              {:type :info, :f :move, :process :nemesis, :time 3439100818877, :error "indeterminate: ", :index 12247}
              {:type :info, :f :move, :process :nemesis, :time 3439601151878, :index 12252}
              {:type :info, :f :move, :process :nemesis, :time 3439602373507, :error "indeterminate: ", :index 12253}
              {:type :info, :f :move, :process :nemesis, :time 3440102700199, :index 12260}
              {:type :info, :f :move, :process :nemesis, :time 3440104329945, :error "indeterminate: ", :index 12261}
              {:type :info, :f :start, :process :nemesis, :time 3440604627421, :index 12266}
              {:type :info, :f :start, :process :nemesis, :time 3440742706902, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.76.99"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.76.99"}}], :index 12268}
              {:type :info, :f :stop, :process :nemesis, :time 3460743025801, :index 12289}
              {:type :info, :f :stop, :process :nemesis, :time 3461006350632, :value :network-healed, :index 12290}
              {:type :info, :f :move, :process :nemesis, :time 3471006614891, :index 12333}
              {:type :info, :f :move, :process :nemesis, :time 3471007455718, :error "indeterminate: ", :index 12334}
              {:type :info, :f :move, :process :nemesis, :time 3471507920351, :index 12340}
              {:type :info, :f :move, :process :nemesis, :time 3471510557104, :error "indeterminate: ", :index 12341}
              {:type :info, :f :move, :process :nemesis, :time 3472011059631, :index 12346}
              {:type :info, :f :move, :process :nemesis, :time 3472011772967, :error "indeterminate: ", :index 12347}
              {:type :info, :f :start, :process :nemesis, :time 3472512017121, :index 12351}
              {:type :info, :f :start, :process :nemesis, :time 3472641405762, :value [:isolated {"175.24.76.99" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "212.64.86.223" #{"175.24.75.168" "212.64.58.158" "175.24.106.227"}, "175.24.75.168" #{"175.24.76.99" "212.64.86.223"}, "212.64.58.158" #{"175.24.76.99" "212.64.86.223"}, "175.24.106.227" #{"175.24.76.99" "212.64.86.223"}}], :index 12358}
              {:type :info, :f :stop, :process :nemesis, :time 3492641696482, :index 12380}
              {:type :info, :f :stop, :process :nemesis, :time 3492924896095, :value :network-healed, :index 12381}
              {:type :info, :f :move, :process :nemesis, :time 3502925369560, :index 12434}
              {:type :info, :f :move, :process :nemesis, :time 3502926824748, :error "indeterminate: ", :index 12435}
              {:type :info, :f :move, :process :nemesis, :time 3503427169797, :index 12440}
              {:type :info, :f :move, :process :nemesis, :time 3503427695507, :error "indeterminate: ", :index 12441}
              {:type :info, :f :move, :process :nemesis, :time 3503927974531, :index 12447}
              {:type :info, :f :move, :process :nemesis, :time 3503929315916, :error "indeterminate: ", :index 12448}
              {:type :info, :f :start, :process :nemesis, :time 3504429560646, :index 12454}
              {:type :info, :f :start, :process :nemesis, :time 3504565575639, :value [:isolated {"175.24.106.227" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "212.64.58.158" "212.64.86.223"}, "175.24.75.168" #{"175.24.106.227" "175.24.76.99"}, "212.64.58.158" #{"175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.106.227" "175.24.76.99"}}], :index 12455}
              {:type :info, :f :stop, :process :nemesis, :time 3524566686599, :index 12492}
              {:type :info, :f :stop, :process :nemesis, :time 3524828302509, :value :network-healed, :index 12495}
              {:type :info, :f :move, :process :nemesis, :time 3534828675806, :index 12527}
              {:type :info, :f :move, :process :nemesis, :time 3534829248809, :error "indeterminate: ", :index 12528}
              {:type :info, :f :move, :process :nemesis, :time 3535329456885, :index 12531}
              {:type :info, :f :move, :process :nemesis, :time 3535330140482, :error "indeterminate: ", :index 12532}
              {:type :info, :f :move, :process :nemesis, :time 3535830360156, :index 12538}
              {:type :info, :f :move, :process :nemesis, :time 3535831087734, :error "indeterminate: ", :index 12539}
              {:type :info, :f :start, :process :nemesis, :time 3536331256361, :index 12540}
              {:type :info, :f :start, :process :nemesis, :time 3536755398286, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.76.99" #{"175.24.75.168" "175.24.106.227" "212.64.86.223"}, "175.24.75.168" #{"212.64.58.158" "175.24.76.99"}, "175.24.106.227" #{"212.64.58.158" "175.24.76.99"}, "212.64.86.223" #{"212.64.58.158" "175.24.76.99"}}], :index 12547}
              {:type :info, :f :stop, :process :nemesis, :time 3556755949785, :index 12576}
              {:type :info, :f :stop, :process :nemesis, :time 3557211616679, :value :network-healed, :index 12577}
              {:type :info, :f :move, :process :nemesis, :time 3567211904165, :index 12620}
              {:type :info, :f :move, :process :nemesis, :time 3567212728448, :error "indeterminate: ", :index 12621}
              {:type :info, :f :move, :process :nemesis, :time 3567712958458, :index 12627}
              {:type :info, :f :move, :process :nemesis, :time 3567713797902, :error "indeterminate: ", :index 12628}
              {:type :info, :f :move, :process :nemesis, :time 3568214049589, :index 12638}
              {:type :info, :f :move, :process :nemesis, :time 3568214731937, :error "indeterminate: ", :index 12639}
              {:type :info, :f :start, :process :nemesis, :time 3568714925338, :index 12645}
              {:type :info, :f :start, :process :nemesis, :time 3568852457013, :value [:isolated {"212.64.58.158" #{"175.24.75.168" "175.24.106.227" "175.24.76.99"}, "212.64.86.223" #{"175.24.75.168" "175.24.106.227" "175.24.76.99"}, "175.24.75.168" #{"212.64.58.158" "212.64.86.223"}, "175.24.106.227" #{"212.64.58.158" "212.64.86.223"}, "175.24.76.99" #{"212.64.58.158" "212.64.86.223"}}], :index 12649}
              {:type :info, :f :stop, :process :nemesis, :time 3588852826597, :index 12676}
              {:type :info, :f :stop, :process :nemesis, :time 3589187472543, :value :network-healed, :index 12677}
              {:type :info, :f :move, :process :nemesis, :time 3599187984623, :index 12729}
              {:type :info, :f :move, :process :nemesis, :time 3599190305261, :error "indeterminate: ", :index 12730}
              {:type :info, :f :move, :process :nemesis, :time 3599690677936, :index 12736}
              {:type :info, :f :move, :process :nemesis, :time 3599691384053, :error "indeterminate: ", :index 12737}
              {:type :info, :f :move, :process :nemesis, :time 3600191570220, :index 12742}
              {:type :info, :f :move, :process :nemesis, :time 3600193108475, :error "indeterminate: ", :index 12743}
              ])


