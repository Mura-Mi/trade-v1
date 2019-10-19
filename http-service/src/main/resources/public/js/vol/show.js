const ctx = document.getElementById('mychart').getContext('2d');

fetch("/vol.json")
    .then(r => r.json())
    .then(myJson => {
            console.log(myJson);
            new Chart(ctx, {
                type: 'line',
                data: {
                    datasets: [
                        {
                            label: 'vol',
                            data: myJson.map(d => d.vol)
                        }
                    ],
                    labels: myJson.map(d => d.date)
                },
                options: {
                    scales: {
                        xAxes: [
                            {
                                type: 'time',
                                distribution: 'series',
                                time: {
                                    parser: 'YYYY-MM-DD'
                                }
                            }
                        ]
                    }
                }
            })
        }
    );

;
