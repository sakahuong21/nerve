package io.nuls.dex.storage.impl;

import io.nuls.base.basic.NulsByteBuffer;
import io.nuls.base.data.NulsHash;
import io.nuls.core.core.annotation.Component;
import io.nuls.core.exception.NulsException;
import io.nuls.core.log.Log;
import io.nuls.core.rockdb.model.Entry;
import io.nuls.core.rockdb.service.RocksDBService;
import io.nuls.dex.context.DexDBConstant;
import io.nuls.dex.model.po.CoinTradingPo;
import io.nuls.dex.storage.CoinTradingStorageService;
import io.nuls.dex.util.LoggerUtil;

import java.util.ArrayList;
import java.util.List;

@Component
public class CoinTradingStorageServiceImpl implements CoinTradingStorageService {

    @Override
    public void save(CoinTradingPo tradingPo) throws Exception {
        //存储交易对实体信息
        RocksDBService.put(DexDBConstant.DB_NAME_COIN_TRADING, tradingPo.getHash().getBytes(), tradingPo.serialize());
    }

    @Override
    public CoinTradingPo query(NulsHash hash) throws NulsException {
        byte[] value = RocksDBService.get(DexDBConstant.DB_NAME_COIN_TRADING, hash.getBytes());
        if (value == null) {
            return null;
        }
        CoinTradingPo po = new CoinTradingPo();
        po.parse(new NulsByteBuffer(value));
        po.setHash(hash);
        return po;
    }

    @Override
    public CoinTradingPo query(String coinTradingKey) {
        byte[] hash = RocksDBService.get(DexDBConstant.DB_NAME_COIN_TRADING, coinTradingKey.getBytes());
        if (hash == null) {
            return null;
        }
        byte[] value = RocksDBService.get(DexDBConstant.DB_NAME_COIN_TRADING, hash);
        CoinTradingPo po = new CoinTradingPo();
        try {
            po.parse(new NulsByteBuffer(value));
            po.setHash(new NulsHash(hash));
            return po;
        } catch (NulsException e) {
            LoggerUtil.dexLog.error(e);
            return null;
        }
    }

    @Override
    public List<CoinTradingPo> queryAll() throws NulsException {
        List<CoinTradingPo> tradingPoList = new ArrayList<>();
        List<Entry<byte[], byte[]>> list = RocksDBService.entryList(DexDBConstant.DB_NAME_COIN_TRADING);
        if (list != null && !list.isEmpty()) {
            for (Entry<byte[], byte[]> entry : list) {
                CoinTradingPo tradingPo = new CoinTradingPo();
                tradingPo.parse(new NulsByteBuffer(entry.getValue()));
                NulsHash hash = new NulsHash(entry.getKey());
                tradingPo.setHash(hash);
                tradingPoList.add(tradingPo);
            }
        }
        return tradingPoList;
    }

    @Override
    public void delete(CoinTradingPo tradingPo) throws Exception {
        RocksDBService.delete(DexDBConstant.DB_NAME_COIN_TRADING, tradingPo.getHash().getBytes());
    }
}
