const a = {
  "nn": "mule",
  "c": [
    {
      "nn": "flow",
      "c": null,
      "a": [
        [
          "tag-name",
          "flow"
        ],
        [
          "description",
          "example:/http-incoming-flow"
        ],
        [
          "regular-content",
          [
            {
              "nn": "http:listener",
              "c": null,
              "a": [
                [
                  "tag-name",
                  "http:listener"
                ],
                [
                  "description",
                  "HTTP"
                ],
                [
                  "attributes",
                  []
                ]
              ]
            },
            {
              "nn": "set-variable",
              "c": null,
              "a": [
                [
                  "tag-name",
                  "set-variable"
                ],
                [
                  "description",
                  "Set Z level to 9000"
                ],
                [
                  "attributes",
                  []
                ]
              ]
            },
            {
              "nn": "set-payload",
              "c": null,
              "a": [
                [
                  "tag-name",
                  "set-payload"
                ],
                [
                  "description",
                  "Overwrite the payload"
                ],
                [
                  "attributes",
                  []
                ]
              ]
            },
            {
              "nn": "flow-ref",
              "c": null,
              "a": [
                [
                  "tag-name",
                  "flow-ref"
                ],
                [
                  "description",
                  "example:/expunge-convolvers"
                ],
                [
                  "attributes",
                  []
                ]
              ]
            },
            {
              "nn": "expression-filter",
              "c": null,
              "a": [
                [
                  "tag-name",
                  "expression-filter"
                ],
                [
                  "description",
                  "Filter recalcitrant ions"
                ],
                [
                  "attributes",
                  []
                ]
              ]
            },
            {
              "nn": "foreach",
              "c": [
                {
                  "nn": "flow-ref",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "flow-ref"
                    ],
                    [
                      "description",
                      "example:/strain-overflow-noodle-requests"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                }
              ],
              "a": [
                [
                  "tag-name",
                  "foreach"
                ],
                [
                  "description",
                  "For Each"
                ],
                [
                  "attributes",
                  [
                    "horizontal"
                  ]
                ]
              ]
            },
            {
              "nn": "set-payload",
              "c": null,
              "a": [
                [
                  "tag-name",
                  "set-payload"
                ],
                [
                  "description",
                  "Server Noodles"
                ],
                [
                  "attributes",
                  []
                ]
              ]
            }
          ]
        ],
        [
          "error-content",
          [
            {
              "nn": "catch-exception-strategy",
              "c": [
                {
                  "nn": "set-payload",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "set-payload"
                    ],
                    [
                      "description",
                      "Nothing to see here"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                },
                {
                  "nn": "message-properties-transformer",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "message-properties-transformer"
                    ],
                    [
                      "description",
                      "Nor here"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                }
              ],
              "a": [
                [
                  "tag-name",
                  "catch-exception-strategy"
                ],
                [
                  "description",
                  "Catch Exception Strategy"
                ],
                [
                  "attributes",
                  [
                    "error-handler"
                  ]
                ]
              ]
            }
          ]
        ],
        [
          "attributes",
          [
            "vertical"
          ]
        ]
      ]
    },
    {
      "nn": "sub-flow",
      "c": [
        {
          "nn": "db:no-operation-selected",
          "c": null,
          "a": [
            [
              "tag-name",
              "db:no-operation-selected"
            ],
            [
              "description",
              "Flush the convolver prisms"
            ],
            [
              "attributes",
              []
            ]
          ]
        },
        {
          "nn": "choice",
          "c": [
            {
              "nn": "when",
              "c": [
                {
                  "nn": "s3:no-operation-selected",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "s3:no-operation-selected"
                    ],
                    [
                      "description",
                      "Fetch more prisms"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                },
                {
                  "nn": "logger",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "logger"
                    ],
                    [
                      "description",
                      "Log number of fanglehockeys"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                }
              ],
              "a": [
                [
                  "tag-name",
                  "when"
                ],
                [
                  "description",
                  null
                ],
                [
                  "attributes",
                  [
                    "horizontal"
                  ]
                ]
              ]
            },
            {
              "nn": "otherwise",
              "c": [
                {
                  "nn": "logger",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "logger"
                    ],
                    [
                      "description",
                      "Log Error"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                }
              ],
              "a": [
                [
                  "tag-name",
                  "otherwise"
                ],
                [
                  "description",
                  null
                ],
                [
                  "attributes",
                  [
                    "horizontal"
                  ]
                ]
              ]
            }
          ],
          "a": [
            [
              "tag-name",
              "choice"
            ],
            [
              "description",
              "Did the flush occur?"
            ],
            [
              "attributes",
              [
                "vertical"
              ]
            ]
          ]
        }
      ],
      "a": [
        [
          "tag-name",
          "sub-flow"
        ],
        [
          "description",
          "example:/expunge-convolvers"
        ],
        [
          "attributes",
          [
            "horizontal"
          ]
        ]
      ]
    },
    {
      "nn": "sub-flow",
      "c": [
        {
          "nn": "scatter-gather",
          "c": [
            {
              "nn": "processor-chain",
              "c": [
                {
                  "nn": "http:request",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "http:request"
                    ],
                    [
                      "description",
                      "Clean Carburetor 1"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                },
                {
                  "nn": "mulexml:xml-to-dom-transformer",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "mulexml:xml-to-dom-transformer"
                    ],
                    [
                      "description",
                      "XML to DOM"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                }
              ],
              "a": [
                [
                  "tag-name",
                  "processor-chain"
                ],
                [
                  "description",
                  "Processor Chain"
                ],
                [
                  "attributes",
                  [
                    "horizontal"
                  ]
                ]
              ]
            },
            {
              "nn": "processor-chain",
              "c": [
                {
                  "nn": "http:request",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "http:request"
                    ],
                    [
                      "description",
                      "Oops this has a really long name"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                },
                {
                  "nn": "set-payload",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "set-payload"
                    ],
                    [
                      "description",
                      "Set Payload"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                }
              ],
              "a": [
                [
                  "tag-name",
                  "processor-chain"
                ],
                [
                  "description",
                  null
                ],
                [
                  "attributes",
                  [
                    "horizontal"
                  ]
                ]
              ]
            },
            {
              "nn": "http:request",
              "c": null,
              "a": [
                [
                  "tag-name",
                  "http:request"
                ],
                [
                  "description",
                  "Clean Carburetor 3"
                ],
                [
                  "attributes",
                  []
                ]
              ]
            }
          ],
          "a": [
            [
              "tag-name",
              "scatter-gather"
            ],
            [
              "description",
              "Clean carburetors"
            ],
            [
              "attributes",
              [
                "vertical"
              ]
            ]
          ]
        },
        {
          "nn": "http:request",
          "c": null,
          "a": [
            [
              "tag-name",
              "http:request"
            ],
            [
              "description",
              "Start Internal Noodle Engine"
            ],
            [
              "attributes",
              []
            ]
          ]
        }
      ],
      "a": [
        [
          "tag-name",
          "sub-flow"
        ],
        [
          "description",
          "example:/strain-overflow-noodle-requests"
        ],
        [
          "attributes",
          [
            "horizontal"
          ]
        ]
      ]
    }
  ],
  "a": [
    [
      "tag-name",
      "mule"
    ],
    [
      "description",
      null
    ],
    [
      "attributes",
      [
        "vertical",
        "root"
      ]
    ]
  ]
}

const b = {
  "nn": "mule",
  "c": [
    {
      "nn": "flow",
      "c": null,
      "a": [
        [
          "tag-name",
          "flow"
        ],
        [
          "description",
          "example:/http-incoming-flow"
        ],
        [
          "regular-content",
          [
            {
              "nn": "http:listener",
              "c": null,
              "a": [
                [
                  "tag-name",
                  "http:listener"
                ],
                [
                  "description",
                  "HTTP"
                ],
                [
                  "attributes",
                  []
                ]
              ]
            },
            {
              "nn": "set-variable",
              "c": null,
              "a": [
                [
                  "tag-name",
                  "set-variable"
                ],
                [
                  "description",
                  "Set Z level to 7000"
                ],
                [
                  "attributes",
                  []
                ]
              ]
            },
            {
              "nn": "set-payload",
              "c": null,
              "a": [
                [
                  "tag-name",
                  "set-payload"
                ],
                [
                  "description",
                  "Overwrite the payload"
                ],
                [
                  "attributes",
                  []
                ]
              ]
            },
            {
              "nn": "flow-ref",
              "c": null,
              "a": [
                [
                  "tag-name",
                  "flow-ref"
                ],
                [
                  "description",
                  "example:/expunge-convolvers"
                ],
                [
                  "attributes",
                  []
                ]
              ]
            },
            {
              "nn": "expression-filter",
              "c": null,
              "a": [
                [
                  "tag-name",
                  "expression-filter"
                ],
                [
                  "description",
                  "Filter recalcitrant ions"
                ],
                [
                  "attributes",
                  []
                ]
              ]
            },
            {
              "nn": "expression-filter",
              "c": null,
              "a": [
                [
                  "tag-name",
                  "expression-filter"
                ],
                [
                  "description",
                  "Filter overheated plasma purges"
                ],
                [
                  "attributes",
                  []
                ]
              ]
            },
            {
              "nn": "foreach",
              "c": [
                {
                  "nn": "set-payload",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "set-payload"
                    ],
                    [
                      "description",
                      "Before the straining"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                },
                {
                  "nn": "flow-ref",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "flow-ref"
                    ],
                    [
                      "description",
                      "example:/strain-overflow-noodle-requests"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                },
                {
                  "nn": "set-payload",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "set-payload"
                    ],
                    [
                      "description",
                      "After the straining"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                }
              ],
              "a": [
                [
                  "tag-name",
                  "foreach"
                ],
                [
                  "description",
                  "For Each"
                ],
                [
                  "attributes",
                  [
                    "horizontal"
                  ]
                ]
              ]
            }
          ]
        ],
        [
          "error-content",
          [
            {
              "nn": "catch-exception-strategy",
              "c": [
                {
                  "nn": "set-payload",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "set-payload"
                    ],
                    [
                      "description",
                      "Nothing to see here"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                },
                {
                  "nn": "message-properties-transformer",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "message-properties-transformer"
                    ],
                    [
                      "description",
                      "Nor here"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                }
              ],
              "a": [
                [
                  "tag-name",
                  "catch-exception-strategy"
                ],
                [
                  "description",
                  "Catch Exception Strategy"
                ],
                [
                  "attributes",
                  [
                    "error-handler"
                  ]
                ]
              ]
            }
          ]
        ],
        [
          "attributes",
          [
            "vertical"
          ]
        ]
      ]
    },
    {
      "nn": "sub-flow",
      "c": [
        {
          "nn": "db:no-operation-selected",
          "c": null,
          "a": [
            [
              "tag-name",
              "db:no-operation-selected"
            ],
            [
              "description",
              "Flush the convolver prisms"
            ],
            [
              "attributes",
              []
            ]
          ]
        },
        {
          "nn": "choice",
          "c": [
            {
              "nn": "when",
              "c": [
                {
                  "nn": "s3:no-operation-selected",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "s3:no-operation-selected"
                    ],
                    [
                      "description",
                      "Fetch more prisms"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                },
                {
                  "nn": "logger",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "logger"
                    ],
                    [
                      "description",
                      "Log number of fanglehockeys"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                }
              ],
              "a": [
                [
                  "tag-name",
                  "when"
                ],
                [
                  "description",
                  null
                ],
                [
                  "attributes",
                  [
                    "horizontal"
                  ]
                ]
              ]
            },
            {
              "nn": "otherwise",
              "c": [
                {
                  "nn": "logger",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "logger"
                    ],
                    [
                      "description",
                      "Log Error"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                }
              ],
              "a": [
                [
                  "tag-name",
                  "otherwise"
                ],
                [
                  "description",
                  null
                ],
                [
                  "attributes",
                  [
                    "horizontal"
                  ]
                ]
              ]
            }
          ],
          "a": [
            [
              "tag-name",
              "choice"
            ],
            [
              "description",
              "Did the flush occur?"
            ],
            [
              "attributes",
              [
                "vertical"
              ]
            ]
          ]
        }
      ],
      "a": [
        [
          "tag-name",
          "sub-flow"
        ],
        [
          "description",
          "example:/expunge-convolvers"
        ],
        [
          "attributes",
          [
            "horizontal"
          ]
        ]
      ]
    },
    {
      "nn": "sub-flow",
      "c": [
        {
          "nn": "scatter-gather",
          "c": [
            {
              "nn": "processor-chain",
              "c": [
                {
                  "nn": "http:request",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "http:request"
                    ],
                    [
                      "description",
                      "Clean Carburetor 1"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                },
                {
                  "nn": "mulexml:xml-to-dom-transformer",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "mulexml:xml-to-dom-transformer"
                    ],
                    [
                      "description",
                      "XML to DOM"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                }
              ],
              "a": [
                [
                  "tag-name",
                  "processor-chain"
                ],
                [
                  "description",
                  "Processor Chain"
                ],
                [
                  "attributes",
                  [
                    "horizontal"
                  ]
                ]
              ]
            },
            {
              "nn": "processor-chain",
              "c": [
                {
                  "nn": "http:request",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "http:request"
                    ],
                    [
                      "description",
                      "Oops this has a really long name"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                },
                {
                  "nn": "set-payload",
                  "c": null,
                  "a": [
                    [
                      "tag-name",
                      "set-payload"
                    ],
                    [
                      "description",
                      "Set Payload"
                    ],
                    [
                      "attributes",
                      []
                    ]
                  ]
                }
              ],
              "a": [
                [
                  "tag-name",
                  "processor-chain"
                ],
                [
                  "description",
                  null
                ],
                [
                  "attributes",
                  [
                    "horizontal"
                  ]
                ]
              ]
            },
            {
              "nn": "http:request",
              "c": null,
              "a": [
                [
                  "tag-name",
                  "http:request"
                ],
                [
                  "description",
                  "Clean Carburetor 3"
                ],
                [
                  "attributes",
                  []
                ]
              ]
            }
          ],
          "a": [
            [
              "tag-name",
              "scatter-gather"
            ],
            [
              "description",
              "Clean carburetors"
            ],
            [
              "attributes",
              [
                "vertical"
              ]
            ]
          ]
        },
        {
          "nn": "http:request",
          "c": null,
          "a": [
            [
              "tag-name",
              "http:request"
            ],
            [
              "description",
              "Start Internal Noodle Engine"
            ],
            [
              "attributes",
              []
            ]
          ]
        }
      ],
      "a": [
        [
          "tag-name",
          "sub-flow"
        ],
        [
          "description",
          "example:/strain-overflow-noodle-requests"
        ],
        [
          "attributes",
          [
            "horizontal"
          ]
        ]
      ]
    }
  ],
  "a": [
    [
      "tag-name",
      "mule"
    ],
    [
      "description",
      null
    ],
    [
      "attributes",
      [
        "vertical",
        "root"
      ]
    ]
  ]
}

