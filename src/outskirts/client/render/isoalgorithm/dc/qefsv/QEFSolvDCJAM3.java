package outskirts.client.render.isoalgorithm.dc.qefsv;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.StringUtils;
import outskirts.util.vector.Vector3f;

import java.util.Arrays;
import java.util.List;

// http://www.oocities.org/tzukkers/isosurf/isosurfaces.html also a classic good impl
// http://web.archive.org/web/20150519011320/http://www.tatwood.net/articles/7/dual_contour use of QR decomposation.
public class QEFSolvDCJAM3 {

    public static List<Vector3f> TEST_Pi = Arrays.asList(
            new Vector3f(-4.566275f, 1.0f, -4.0f),
            new Vector3f(-4.0f, 1.709915f, -4.0f),
            new Vector3f(-4.0f, 1.0f, -4.5291433f)
    );
    public static List<Vector3f> TEST_Ni = Arrays.asList(
            new Vector3f(0.67689246f, -0.41498968f, 0.60794747f),
            new Vector3f(0.6664056f, -0.6913128f, 0.2792672f),
            new Vector3f(0.6433687f, -0.27337834f, 0.71508116f)
    );

    /*
     * emilk, online Ax=b: [175.63477, 129.16504, -117.15137]  FAILED. basic sphere stable, 3d unstable.
     *
     * DC-JA-3: [-4.263061, 1.3986124, -4.111558]  CALC STABLE
     *
     * LIN20 XNA, NKG CPP OOP: [-4.1887584, 1.2366384, -4.176381]
     *
     * BruteForce Iter: [-4.566275, 1.0, -4.5291433]
     *
     * NKG OPP: [-4.258465, 1.4018791, -4.114444, 1.0]
     *
     */

    public static void main(String[] args) {

        System.out.println(wCalcQEF( TEST_Pi , TEST_Ni));

    }

    public static Vector3f wCalcQEF(List<Vector3f> verts, List<Vector3f> norms) {
        float[][] vts = new float[verts.size()][3];
        float[][] nms = new float[verts.size()][3];
        for (int i=0; i<verts.size(); i++) {
            vts[i][0] = verts.get(i).x;
            vts[i][1] = verts.get(i).y;
            vts[i][2] = verts.get(i).z;
            nms[i][0] = norms.get(i).x;
            nms[i][1] = norms.get(i).y;
            nms[i][2] = norms.get(i).z;
        }
        float[] mp = calcQEF(vts, nms, AABB.bounding(verts, null) );
        return new Vector3f(mp[0], mp[1], mp[2]);
    }

    private static float[] calcQEF( float[][] inters, float[][] norms , AABB aabb ) {
        int numint = inters.length;

        float[] ata = new float[6];
        float[] atb = new float[3];
        float btb = 0;

        float[] pt ={0,0,0} ;

        for (int i = 0 ; i < numint ; i ++ )
        {
            float[] norm = norms[i].clone() ;
            float[] p = inters[i].clone() ;
            // printf("Norm: %f, %f, %f Pts: %f, %f, %f\n", norm[0], norm[1], norm[2], p[0], p[1], p[2] ) ;

            // QEF
            ata[ 0 ] += ( norm[ 0 ] * norm[ 0 ] );
            ata[ 1 ] += ( norm[ 0 ] * norm[ 1 ] );
            ata[ 2 ] += ( norm[ 0 ] * norm[ 2 ] );
            ata[ 3 ] += ( norm[ 1 ] * norm[ 1 ] );
            ata[ 4 ] += ( norm[ 1 ] * norm[ 2 ] );
            ata[ 5 ] += ( norm[ 2 ] * norm[ 2 ] );

            double pn = p[0] * norm[0] + p[1] * norm[1] + p[2] * norm[2] ;

            atb[ 0 ] += (float) ( norm[ 0 ] * pn ) ;
            atb[ 1 ] += (float) ( norm[ 1 ] * pn ) ;
            atb[ 2 ] += (float) ( norm[ 2 ] * pn ) ;

            btb += (float) pn * (float) pn ;

            // Minimizer
            pt[0] += p[0] ;
            pt[1] += p[1] ;
            pt[2] += p[2] ;
        }

        pt[0] /= numint ;
        pt[1] /= numint ;
        pt[2] /= numint ;

        // Solve
//        float[] mat = new float[10] ;
//        BoundingBoxf box = new BoundingBoxf() ;
//        box.begin.x = (float) st[0] ;
//        box.begin.y = (float) st[1] ;
//        box.begin.z = (float) st[2] ;
//        box.end.x = (float) st[0] + len ;
//        box.end.y = (float) st[1] + len ;
//        box.end.z = (float) st[2] + len ;

        float[] mp = new float[3];

        float error = _calcPoint( ata, atb, btb, pt, mp ) ;

//        System.out.println("error: "+error);
//        System.out.println("mp: "+ Arrays.toString(mp));

        boolean CLAMP = true;
        if(CLAMP)
        {
//            if ( mp[0] < st[0] || mp[1] < st[1] || mp[2] < st[2] ||
//                    mp[0] > st[0] + len || mp[1] > st[1] + len || mp[2] > st[2] + len )
            if (!aabb.contains( new Vector3f(mp), 0.00001f ))
            {
                mp[0] = pt[0] ;
                mp[1] = pt[1] ;
                mp[2] = pt[2] ;
            }
        }
        return mp;
    }


    // method 3.
    private static float _calcPoint( float[] halfA, float[] b, float btb, float[] midpoint, float[] rvalue) {
        float[] newB = new float[ 3 ];
        float[][] a = new float[ 3 ] [ 3 ];
        float[][] inv = new float[ 3 ] [ 3 ];
        float[] w = new float[ 3 ];
        float[][] u = new float[ 3 ] [ 3 ];

        a [ 0 ] [ 0 ] = halfA [ 0 ];
        a [ 0 ] [ 1 ] = halfA [ 1 ];
        a [ 0 ] [ 2 ] = halfA [ 2 ];
        a [ 1 ] [ 1 ] = halfA [ 3 ];
        a [ 1 ] [ 2 ] = halfA [ 4 ];
        a [ 1 ] [ 0 ] = halfA [ 1 ];
        a [ 2 ] [ 0 ] = halfA [ 2 ];
        a [ 2 ] [ 1 ] = halfA [ 4 ];
        a [ 2 ] [ 2 ] = halfA [ 5 ];

        matInverse ( a, midpoint, inv, w, u );


        newB [ 0 ] = b [ 0 ] - a [ 0 ] [ 0 ] * midpoint [ 0 ] - a [ 0 ] [ 1 ] * midpoint [ 1 ] - a [ 0 ] [ 2 ] * midpoint [ 2 ];
        newB [ 1 ] = b [ 1 ] - a [ 1 ] [ 0 ] * midpoint [ 0 ] - a [ 1 ] [ 1 ] * midpoint [ 1 ] - a [ 1 ] [ 2 ] * midpoint [ 2 ];
        newB [ 2 ] = b [ 2 ] - a [ 2 ] [ 0 ] * midpoint [ 0 ] - a [ 2 ] [ 1 ] * midpoint [ 1 ] - a [ 2 ] [ 2 ] * midpoint [ 2 ];

        rvalue [ 0 ] = inv [ 0 ] [ 0 ] * newB [ 0 ] + inv [ 1 ] [ 0 ] * newB [ 1 ] + inv [ 2 ] [ 0 ] * newB [ 2 ] + midpoint [ 0 ];
        rvalue [ 1 ] = inv [ 0 ] [ 1 ] * newB [ 0 ] + inv [ 1 ] [ 1 ] * newB [ 1 ] + inv [ 2 ] [ 1 ] * newB [ 2 ] + midpoint [ 1 ];
        rvalue [ 2 ] = inv [ 0 ] [ 2 ] * newB [ 0 ] + inv [ 1 ] [ 2 ] * newB [ 1 ] + inv [ 2 ] [ 2 ] * newB [ 2 ] + midpoint [ 2 ];

        return calcError ( a, b, btb, rvalue );
    }


    /**
     * Inverts a 3x3 symmetric matrix by computing the pseudo-inverse.
     *
     * @param mat the matrix to invert
     * @param midpoint the point to minimize towards
     * @param rvalue the variable to store the pseudo-inverse in
     * @param w the place to store the inverse of the eigenvalues
     * @param u the place to store the eigenvectors
     */
    public static void matInverse ( float[][] mat, float[] midpoint, float[][] rvalue, float[] w, float[][] u )
    {
        // there is an implicit assumption that mat is symmetric and real
        // U and V in the SVD will then be the same matrix whose rows are the eigenvectors of mat
        // W will just be the eigenvalues of mat
//		float w [ 3 ];
//		float u [ 3 ] [ 3 ];
        int i;

        jacobi ( mat, w, u );

        if ( w [ 0 ] == 0.0f )
        {
//			printf ( "error: largest eigenvalue is 0!\n" );
        }
        else
        {
            for ( i = 1; i < 3; i++ )
            {
                if ( w [ i ] < 0.001f ) // / w [ 0 ] < TOLERANCE )
                {
                    w [ i ] = 0;
                }
                else
                {
                    w [ i ] = 1.0f / w [ i ];
                }
            }
            w [ 0 ] = 1.0f / w [ 0 ];
        }

        rvalue [ 0 ] [ 0 ] = w [ 0 ] * u [ 0 ] [ 0 ] * u [ 0 ] [ 0 ] +
                w [ 1 ] * u [ 1 ] [ 0 ] * u [ 1 ] [ 0 ] +
                w [ 2 ] * u [ 2 ] [ 0 ] * u [ 2 ] [ 0 ];
        rvalue [ 0 ] [ 1 ] = w [ 0 ] * u [ 0 ] [ 0 ] * u [ 0 ] [ 1 ] +
                w [ 1 ] * u [ 1 ] [ 0 ] * u [ 1 ] [ 1 ] +
                w [ 2 ] * u [ 2 ] [ 0 ] * u [ 2 ] [ 1 ];
        rvalue [ 0 ] [ 2 ] = w [ 0 ] * u [ 0 ] [ 0 ] * u [ 0 ] [ 2 ] +
                w [ 1 ] * u [ 1 ] [ 0 ] * u [ 1 ] [ 2 ] +
                w [ 2 ] * u [ 2 ] [ 0 ] * u [ 2 ] [ 2 ];
        rvalue [ 1 ] [ 0 ] = w [ 0 ] * u [ 0 ] [ 1 ] * u [ 0 ] [ 0 ] +
                w [ 1 ] * u [ 1 ] [ 1 ] * u [ 1 ] [ 0 ] +
                w [ 2 ] * u [ 2 ] [ 1 ] * u [ 2 ] [ 0 ];
        rvalue [ 1 ] [ 1 ] = w [ 0 ] * u [ 0 ] [ 1 ] * u [ 0 ] [ 1 ] +
                w [ 1 ] * u [ 1 ] [ 1 ] * u [ 1 ] [ 1 ] +
                w [ 2 ] * u [ 2 ] [ 1 ] * u [ 2 ] [ 1 ];
        rvalue [ 1 ] [ 2 ] = w [ 0 ] * u [ 0 ] [ 1 ] * u [ 0 ] [ 2 ] +
                w [ 1 ] * u [ 1 ] [ 1 ] * u [ 1 ] [ 2 ] +
                w [ 2 ] * u [ 2 ] [ 1 ] * u [ 2 ] [ 2 ];
        rvalue [ 2 ] [ 0 ] = w [ 0 ] * u [ 0 ] [ 2 ] * u [ 0 ] [ 0 ] +
                w [ 1 ] * u [ 1 ] [ 2 ] * u [ 1 ] [ 0 ] +
                w [ 2 ] * u [ 2 ] [ 2 ] * u [ 2 ] [ 0 ];
        rvalue [ 2 ] [ 1 ] = w [ 0 ] * u [ 0 ] [ 2 ] * u [ 0 ] [ 1 ] +
                w [ 1 ] * u [ 1 ] [ 2 ] * u [ 1 ] [ 1 ] +
                w [ 2 ] * u [ 2 ] [ 2 ] * u [ 2 ] [ 1 ];
        rvalue [ 2 ] [ 2 ] = w [ 0 ] * u [ 0 ] [ 2 ] * u [ 0 ] [ 2 ] +
                w [ 1 ] * u [ 1 ] [ 2 ] * u [ 1 ] [ 2 ] +
                w [ 2 ] * u [ 2 ] [ 2 ] * u [ 2 ] [ 2 ];
    }


    /**
     * Uses a jacobi method to return the eigenvectors and eigenvalues
     * of a 3x3 symmetric matrix.  Note: "a" will be destroyed in this
     * process.  "d" will contain the eigenvalues sorted in order of
     * decreasing modulus and v will contain the corresponding eigenvectors.
     *
     *  param a the 3x3 symmetric matrix to calculate the eigensystem for
     * @param d the variable to hold the eigenvalues
     * @param v the variables to hold the eigenvectors
     */
    public static void jacobi ( float[][] u, float[] d, float[][] v )
    {
        int j, iq, ip, i;
        float tresh, theta, tau, t, sm, s, h, g, c;
        float[] b = new float[ 3 ], z =new float[ 3 ];
        float[][] a = new float [ 3 ] [ 3 ];

        a [ 0 ] [ 0 ] = u [ 0 ] [ 0 ];
        a [ 0 ] [ 1 ] = u [ 0 ] [ 1 ];
        a [ 0 ] [ 2 ] = u [ 0 ] [ 2 ];
        a [ 1 ] [ 0 ] = u [ 1 ] [ 0 ];
        a [ 1 ] [ 1 ] = u [ 1 ] [ 1 ];
        a [ 1 ] [ 2 ] = u [ 1 ] [ 2 ];
        a [ 2 ] [ 0 ] = u [ 2 ] [ 0 ];
        a [ 2 ] [ 1 ] = u [ 2 ] [ 1 ];
        a [ 2 ] [ 2 ] = u [ 2 ] [ 2 ];

        for ( ip = 0; ip < 3; ip++ )
        {
            for ( iq = 0; iq < 3; iq++ )
            {
                v [ ip ] [ iq ] = 0.0f;
            }
            v [ ip ] [ ip ] = 1.0f;
        }

        for ( ip = 0; ip < 3; ip++ )
        {
            b [ ip ] = a [ ip ] [ ip ];
            d [ ip ] = b [ ip ];
            z [ ip ] = 0.0f;
        }

        for ( i = 1; i <= 50; i++ )
        {
            sm = 0.0f;
            for ( ip = 0; ip < 2; ip++ )
            {
                for ( iq = ip + 1; iq < 3; iq++ )
                {
                    sm += (float)Math.abs ( a [ ip ] [ iq ] );
                }
            }

            if ( sm == 0.0f )
            {
                // sort the stupid things and transpose
                a [ 0 ] [ 0 ] = v [ 0 ] [ 0 ];
                a [ 0 ] [ 1 ] = v [ 1 ] [ 0 ];
                a [ 0 ] [ 2 ] = v [ 2 ] [ 0 ];
                a [ 1 ] [ 0 ] = v [ 0 ] [ 1 ];
                a [ 1 ] [ 1 ] = v [ 1 ] [ 1 ];
                a [ 1 ] [ 2 ] = v [ 2 ] [ 1 ];
                a [ 2 ] [ 0 ] = v [ 0 ] [ 2 ];
                a [ 2 ] [ 1 ] = v [ 1 ] [ 2 ];
                a [ 2 ] [ 2 ] = v [ 2 ] [ 2 ];

                if ( Math.abs ( d [ 0 ] ) < Math.abs ( d [ 1 ] ) )
                {
                    sm = d [ 0 ];
                    d [ 0 ] = d [ 1 ];
                    d [ 1 ] = sm;

                    sm = a [ 0 ] [ 0 ];
                    a [ 0 ] [ 0 ] = a [ 1 ] [ 0 ];
                    a [ 1 ] [ 0 ] = sm;
                    sm = a [ 0 ] [ 1 ];
                    a [ 0 ] [ 1 ] = a [ 1 ] [ 1 ];
                    a [ 1 ] [ 1 ] = sm;
                    sm = a [ 0 ] [ 2 ];
                    a [ 0 ] [ 2 ] = a [ 1 ] [ 2 ];
                    a [ 1 ] [ 2 ] = sm;
                }
                if ( Math.abs ( d [ 1 ] ) < Math.abs ( d [ 2 ] ) )
                {
                    sm = d [ 1 ];
                    d [ 1 ] = d [ 2 ];
                    d [ 2 ] = sm;

                    sm = a [ 1 ] [ 0 ];
                    a [ 1] [ 0 ] = a [ 2 ] [ 0 ];
                    a [ 2 ] [ 0 ] = sm;
                    sm = a [ 1 ] [ 1 ];
                    a [ 1 ] [ 1 ] = a [ 2 ] [ 1 ];
                    a [ 2 ] [ 1 ] = sm;
                    sm = a [ 1 ] [ 2 ];
                    a [ 1 ] [ 2 ] = a [ 2 ] [ 2 ];
                    a [ 2 ] [ 2 ] = sm;
                }
                if ( Math.abs ( d [ 0 ] ) < Math.abs ( d [ 1 ] ) )
                {
                    sm = d [ 0 ];
                    d [ 0 ] = d [ 1 ];
                    d [ 1 ] = sm;

                    sm = a [ 0 ] [ 0 ];
                    a [ 0 ] [ 0 ] = a [ 1 ] [ 0 ];
                    a [ 1 ] [ 0 ] = sm;
                    sm = a [ 0 ] [ 1 ];
                    a [ 0 ] [ 1 ] = a [ 1 ] [ 1 ];
                    a [ 1 ] [ 1 ] = sm;
                    sm = a [ 0 ] [ 2 ];
                    a [ 0 ] [ 2 ] = a [ 1 ] [ 2 ];
                    a [ 1 ] [ 2 ] = sm;
                }

                v [ 0 ] [ 0 ] = a [ 0 ] [ 0 ];
                v [ 0 ] [ 1 ] = a [ 0 ] [ 1 ];
                v [ 0 ] [ 2 ] = a [ 0 ] [ 2 ];
                v [ 1 ] [ 0 ] = a [ 1 ] [ 0 ];
                v [ 1 ] [ 1 ] = a [ 1 ] [ 1 ];
                v [ 1 ] [ 2 ] = a [ 1 ] [ 2 ];
                v [ 2 ] [ 0 ] = a [ 2 ] [ 0 ];
                v [ 2 ] [ 1 ] = a [ 2 ] [ 1 ];
                v [ 2 ] [ 2 ] = a [ 2 ] [ 2 ];

                return;
            }

            if ( i < 4 )
            {
                tresh = 0.2f * sm / 9;
            }
            else
            {
                tresh = 0.0f;
            }

            for ( ip = 0; ip < 2; ip++ )
            {
                for ( iq = ip + 1; iq < 3; iq++ )
                {
                    g = 100.0f * (float)Math.abs ( a [ ip ] [ iq ] );
                    if ( i > 4 && (float)( Math.abs ( d [ ip ] ) + g ) == (float)Math.abs ( d [ ip ] )
                            && (float)( Math.abs ( d [ iq ] ) + g ) == (float)Math.abs ( d [ iq ] ) )
                    {
                        a [ ip ] [ iq ] = 0.0f;
                    }
                    else
                    {
                        if ( Math.abs ( a [ ip ] [ iq ] ) > tresh )
                        {
                            h = d [ iq ] - d [ ip ];
                            if ( (float)( Math.abs ( h ) + g ) == (float)Math.abs ( h ) )
                            {
                                t = ( a [ ip ] [ iq ] ) / h;
                            }
                            else
                            {
                                theta = 0.5f * h / ( a [ ip ] [ iq ] );
                                t = 1.0f / ( (float)Math.abs ( theta ) + (float)Math.sqrt ( 1.0f + theta * theta ) );
                                if ( theta < 0.0f )
                                {
                                    t = -1.0f * t;
                                }
                            }

                            c = 1.0f / (float)Math.sqrt ( 1 + t * t );
                            s = t * c;
                            tau = s / ( 1.0f + c );
                            h = t * a [ ip ] [ iq ];
                            z [ ip ] -= h;
                            z [ iq ] += h;
                            d [ ip ] -= h;
                            d [ iq ] += h;
                            a [ ip ] [ iq ] = 0.0f;
                            for ( j = 0; j <= ip - 1; j++ )
                            {
                                //ROTATE ( a, j, ip, j, iq );
                                g=a[j][ip];h=a[j][iq];a[j][ip]=g-s*(h+g*tau);a[j][iq]=h+s*(g-h*tau);
                            }
                            for ( j = ip + 1; j <= iq - 1; j++ )
                            {
                                //ROTATE ( a, ip, j, j, iq );
                                g=a[ip][j];h=a[j][iq];a[ip][j]=g-s*(h+g*tau);a[j][iq]=h+s*(g-h*tau);
                            }
                            for ( j = iq + 1; j < 3; j++ )
                            {
                                //ROTATE ( a, ip, j, iq, j );
                                g=a[ip][j];h=a[iq][j];a[ip][j]=g-s*(h+g*tau);a[iq][j]=h+s*(g-h*tau);
                            }
                            for ( j = 0; j < 3; j++ )
                            {
                                //ROTATE ( v, j, ip, j, iq );
                                g=v[j][ip];h=v[j][iq];v[j][ip]=g-s*(h+g*tau);v[j][iq]=h+s*(g-h*tau);
                            }
                        }
                    }
                }
            }

            for ( ip = 0; ip < 3; ip++ )
            {
                b [ ip ] += z [ ip ];
                d [ ip ] = b [ ip ];
                z [ ip ] = 0.0f;
            }
        }
        System.out.println ( "too many iterations in jacobi\n" );
        System.exit(1);
    }


    /**
     * Calculates the L2 norm of the residual (the error)
     * (Transpose[A].A).x = Transpose[A].B
     *
     * @param a the matrix Transpose[A].A
     * @param b the matrix Transpose[A].B
     * @param btb the value Transpose[B].B
     * @param point the minimizer found
     *
     * @return the error of the minimizer
     */
    public static float calcError ( float[][] a, float[] b, float btb, float[] point )
    {
        float rvalue = btb;

        rvalue += -2.0f * ( point [ 0 ] * b [ 0 ] + point [ 1 ] * b [ 1 ] + point [ 2 ] * b [ 2 ] );
        rvalue += point [ 0 ] * ( a [ 0 ] [ 0 ] * point [ 0 ] + a [ 0 ] [ 1 ] * point [ 1 ] + a [ 0 ] [ 2 ] * point [ 2 ] );
        rvalue += point [ 1 ] * ( a [ 1 ] [ 0 ] * point [ 0 ] + a [ 1 ] [ 1 ] * point [ 1 ] + a [ 1 ] [ 2 ] * point [ 2 ] );
        rvalue += point [ 2 ] * ( a [ 2 ] [ 0 ] * point [ 0 ] + a [ 2 ] [ 1 ] * point [ 1 ] + a [ 2 ] [ 2 ] * point [ 2 ] );

        return rvalue;
    }
}
