class OptionValuationSet {
  greeks: Greeks;
  payoff: OptionPayoff[]
}

class Greeks {
  marketPrice: number | null;
  delta: number | null;
  vega: number | null;
  theta: number | null;
}

class OptionPayoff {
  atUnderlying: string;
  optionValue: number;
  intrinsic: number;
}
