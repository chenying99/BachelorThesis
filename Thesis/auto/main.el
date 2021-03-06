(TeX-add-style-hook "main"
 (lambda ()
    (LaTeX-add-bibliographies
     "ref/refs")
    (TeX-add-symbols
     '("vc" 1)
     '("reftbl" 1)
     '("reffig" 1))
    (TeX-run-style-hooks
     "algpseudocode"
     "algorithmicx"
     "algorithm"
     "pdfpages"
     "bm"
     "setspace"
     "tikz"
     "comment"
     "thutils"
     "latex2e"
     "thuthesis10"
     "thuthesis"
     "bachelor"
     "nofonts"
     "scanversion"
     "data/cover"
     "data/intro01"
     "data/framework02"
     "data/suffixtree03"
     "data/cluster04"
     "data/template05"
     "data/experiment06"
     "data/future07"
     "data/ack"
     "data/translation")))

