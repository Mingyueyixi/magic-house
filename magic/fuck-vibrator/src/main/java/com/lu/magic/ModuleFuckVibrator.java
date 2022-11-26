package com.lu.magic;

import com.lu.magic.arts.BaseMagic;
import com.lu.magic.arts.FuckVibratorMagic;

public class ModuleFuckVibrator implements IModuleFace {

    @Override
    public BaseMagic loadMagic() {
        return new FuckVibratorMagic();
    }

}


